use near_sdk::borsh::{self, BorshDeserialize, BorshSerialize};
use near_sdk::collections::{UnorderedMap, UnorderedSet};
use near_sdk::AccountId;
use near_sdk::{env, log, near_bindgen, setup_alloc};
mod link;
mod permission;
mod route;
mod service;
use service::{BSH};

use btp_common::BTPAddress;
use permission::Owners;
use route::{Route, Routes};
#[macro_use]
extern crate lazy_static;

use link::{Link, Links};

setup_alloc!();

#[near_bindgen]
#[derive(BorshDeserialize, BorshSerialize)]
pub struct BTPMessageCenter {
    message: Message,
    links: Links,
    routes: Routes,
    bsh: BSH,
    // owners: Owners,
}

#[derive(Default, BorshDeserialize, BorshSerialize)]
pub struct Message {
    payload: Vec<u8>,
}

#[near_bindgen]
impl Default for BTPMessageCenter {
    fn default() -> Self {
        let message: Message = Default::default();
        let links = Links::new();
        let routes = Routes::new();
        let bsh = BSH::new();
        // let owners = Owners::new();
        Self {
            message,
            links,
            routes,
            bsh,
            // owners,
        }
    }
}

#[near_bindgen]
impl BTPMessageCenter {
    pub fn get_message(&self) -> String {
        return String::from_utf8(self.message.payload.clone()).unwrap_or_default();
    }

    pub fn send_message(&mut self, message: String) {
        self.message.payload = message.as_bytes().to_vec()
    }

    // pub fn add_link(&mut self, link: &BTPAddress) {
    //     assert!(
    //         self.owners.is_owner(env::signer_account_id()),
    //         "BMCRevertUnauthorized"
    //     );

    //     self.links
    //         .add_link(link)
    //         .expect_err("BMCRevertUnauthorized");
    // }
    // pub fn add_routes(&mut self, dst: &BTPAddress, link: &BTPAddress) {
    //     self.routes
    //         .add_route(dst, link)
    //         .expect_err("Not able to add route");
    // }

    // pub fn remove_route(&mut self, dst: &BTPAddress) {
    //     assert!(
    //         self.owners.is_owner(env::signer_account_id()),
    //         "BMCRevertUnauthorized"
    //     );
    //     self.routes.remove_route(dst).expect_err("Failed to remove");
    // }

    // pub fn add_owner(&mut self, address: &AccountId) {
    //     assert!(
    //         self.owners.is_owner(env::signer_account_id()),
    //         "BMCRevertUnauthorized"
    //     );

    //     self.owners
    //         .add_owner(address)
    //         .expect_err("failed to add owner");
    // }

    pub fn add_relays(&mut self, link: &BTPAddress, address: &Vec<AccountId>) -> bool {
        self.links
            .set(&link, None, None, None, Some(address.to_vec()))
            .is_ok()
    }

    pub fn add_relay(&mut self, link: &BTPAddress, address: &AccountId) -> bool {
        match self.links.get(&link) {
            Ok(mut link_property) => {
                let _ = link_property.relays.add(address.to_string()).is_ok();
                return self
                    .links
                    .set(&link, None, None, None, Some(link_property.relays.to_vec()))
                    .is_ok();
            }
            Err(error) => {
                log!(error);
            }
        }
        false
    }

    // pub fn remove_relay(&self,link:&BTPAddress, address: &Vec<AccountId>) -> bool {

    // }

    pub fn get_relays(&self, link: &BTPAddress) -> Vec<String> {
        match self.links.get(&link) {
            Ok(link_property) => {
                return link_property.relays.to_vec();
            }

            Err(err) => {
                vec![]
            }
        }

        // let link = self.links.0.get(key: &K);

        // if !link.relays.0.is_empty(){
        //    for v in link.relays.0.iter(){

        //    return v;
        //    }
        // }
    }

    pub fn approve_service(&mut self, service: String, is_accepted: bool) {
        let pendingrq = self.bsh.requests.get().unwrap();

        for i in 0..pendingrq.len() {
            if pendingrq[i] == service.clone() {
                if is_accepted {
                    match self.bsh.services.add(service.clone(), pendingrq[i].clone()) {
                        Ok(true) => println!("service Added"),
                        Ok(false) => println!("service not added"),
                        Err(err) => println!("{}", err),
                    }
                }

                self.bsh.requests.remove(pendingrq[i].clone());
            }

            log!("BMCRevertNotExistRequest");
        }
    }
 
    pub fn remove_service(&mut self, service: String) {
        self.bsh.services.remove(service.clone());
        log!("BMCRevertNotExistsBSH");
    }

    pub fn get_services(&self) {
        // match self.bsh.services.get() {
        //     Ok(value) => println!("{:?}", value),
        //     Err(err) => println!("{}", err),
        // }
    }
}
#[cfg(test)]
mod tests {
    use super::*;
    use near_sdk::MockedBlockchain;
    use near_sdk::{testing_env, VMContext};

    fn get_context(input: Vec<u8>, is_view: bool) -> VMContext {
        VMContext {
            current_account_id: "alice.testnet".to_string(),
            signer_account_id: "robert.testnet".to_string(),
            signer_account_pk: vec![0, 1, 2],
            predecessor_account_id: "jane.testnet".to_string(),
            input,
            block_index: 0,
            block_timestamp: 0,
            account_balance: 0,
            account_locked_balance: 0,
            storage_usage: 0,
            attached_deposit: 0,
            prepaid_gas: 10u64.pow(18),
            random_seed: vec![0, 1, 2],
            is_view,
            output_data_receivers: vec![],
            epoch_height: 19,
        }
    }

    #[test]
    fn send_message() {
        let context = get_context(vec![], false);
        testing_env!(context);
        let mut contract = BTPMessageCenter {
            ..Default::default()
        };
        let mut s = String::from("");
        contract.send_message("dddddd".to_string());
        assert_eq!("dddddd".to_string(), contract.get_message());
    }
    #[test]
    fn relay_handling() {
        let context = get_context(vec![], false);
        testing_env!(context);
        let mut contract = BTPMessageCenter {
            ..Default::default()
        };

        let link =
            BTPAddress("btp://0x1.near/cx87ed9048b594b95199f326fc76e76a9d33dd665b".to_string());

        match contract.links.insert(&link) {
            Ok(res) => println!("{}", res),
            Err(err) => println!("{}", err),
        }
        let address = vec![
            String::from("xyz"),
            String::from("abc"),
            String::from("tuv"),
        ];

        let result = contract.add_relays(&link, &address);

        println!("{:#?}", result);

        let r = contract.get_relays(&link);

        assert_eq!(r, address);

        println!("{:?}", r);
    }
}
