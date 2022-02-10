use super::*;

#[near_bindgen]
impl NativeCoinService {
    // * * * * * * * * * * * * * * * * *
    // * * * * * * * * * * * * * * * * *
    // * * * * * Transactions  * * * * *
    // * * * * * * * * * * * * * * * * *
    // * * * * * * * * * * * * * * * * *

    #[payable]
    pub fn deposit(&mut self) {
        let account = env::predecessor_account_id();
        let amount = env::attached_deposit();
        self.assert_have_minimum_amount(amount);
        let token_id = Self::hash_token_id(&self.native_coin_name);

        let mut balance = match self.balances.get(&account, &token_id) {
            Some(balance) => balance,
            None => AccountBalance::default(),
        };

        self.process_deposit(amount, &mut balance);
        self.balances.set(&account, &token_id, balance);
    }

    #[payable]
    pub fn withdraw(&mut self, token_id: TokenId, amount: U128) {
        // To Prevent Spam
        assert_one_yocto();

        let amount: u128 = amount.into();
        let account = env::predecessor_account_id();

        self.assert_have_minimum_amount(amount);

        let native_coin_id = Self::hash_token_id(&self.native_coin_name);
        self.assert_have_sufficient_deposit(&account, &native_coin_id, amount, None);

        // Check if current account have sufficient balance
        self.assert_have_sufficient_balance(1 + amount);

        let native_coin = self.tokens.get(&token_id).unwrap();

        if native_coin.network() != &self.network {
            ext_nep141::ft_transfer_call_with_storage_check(
                account.clone(),
                amount,
                None,
                native_coin.metadata().uri().to_owned().unwrap(),
                estimate::NO_DEPOSIT,
                estimate::GAS_FOR_MT_TRANSFER_CALL,
            ).then(ext_self::on_withdraw(
                account.clone(),
                amount,
                native_coin_id,
                native_coin.symbol().to_owned(),
                env::current_account_id(),
                estimate::NO_DEPOSIT,
                estimate::GAS_FOR_MT_TRANSFER_CALL,
            ));
        } else {
            Promise::new(account.clone())
                .transfer(amount + 1)
                .then(ext_self::on_withdraw(
                    account.clone(),
                    amount,
                    native_coin_id,
                    native_coin.symbol().to_owned(),
                    env::current_account_id(),
                    estimate::NO_DEPOSIT,
                    estimate::GAS_FOR_MT_TRANSFER_CALL,
                ));
        }
    }

    pub fn reclaim(&mut self, coin_id: TokenId, amount: U128) {
        let amount: u128 = amount.into();
        let account = env::predecessor_account_id();
        self.assert_have_minimum_amount(amount.into());
        self.assert_tokens_exists(&vec![coin_id.clone()]);
        self.assert_have_sufficient_refundable(&account, &coin_id, amount);

        let mut balance = self.balances.get(&account, &coin_id).unwrap();
        balance.refundable_mut().sub(amount).unwrap();
        balance.deposit_mut().add(amount).unwrap();

        self.balances.set(&account, &coin_id, balance);
    }

    pub fn locked_balance_of(&self, owner_id: AccountId, token_id: TokenId) -> U128 {
        self.assert_tokens_exists(&vec![token_id.clone()]);
        let balance = self
            .balances
            .get(&owner_id, &token_id)
            .expect(format!("{}", BshError::AccountNotExist).as_str());
        balance.locked().into()
    }

    pub fn refundable_balance_of(&self, owner_id: AccountId, token_id: TokenId) -> U128 {
        self.assert_tokens_exists(&vec![token_id.clone()]);
        let balance = self
            .balances
            .get(&owner_id, &token_id)
            .expect(format!("{}", BshError::AccountNotExist).as_str());
        balance.refundable().into()
    }

    #[cfg(feature = "testable")]
    pub fn account_balance(
        &self,
        owner_id: AccountId,
        token_id: TokenId,
    ) -> Option<AccountBalance> {
        self.balances.get(&owner_id, &token_id)
    }

    pub fn balance_of(&self, owner_id: AccountId, token_id: TokenId) -> U128 {
        self.assert_tokens_exists(&vec![token_id.clone()]);
        let balance = self
            .balances
            .get(&owner_id, &token_id)
            .expect(format!("{}", BshError::AccountNotExist).as_str());
        balance.deposit().into()
    }

    pub fn on_withdraw(
        &mut self,
        account: AccountId,
        amount: u128,
        native_coin_id: TokenId,
        token_symbol: String,
    ) {
        let mut balance = self.balances.get(&account, &native_coin_id).unwrap();
        balance.deposit_mut().sub(amount).unwrap();
        self.balances
            .set(&account.clone(), &native_coin_id, balance);

        log!(
            "[Withdrawn] Amount : {} by {}  {}",
            amount,
            account,
            token_symbol
        );
    }
}
