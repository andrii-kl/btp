#!/bin/sh
set -e

source transfer_util.sh

DEPOSIT_AMOUNT=2000000
TRANSFER_AMOUNT=1000000
MOONBEAM_PREFUND_PK=39539ab1876910bbf3a223d84a29e28f1cb4e2e456503e7e91ed39b2e7223d68
MOONBEAM_GAS_LIMIT=6721975


deposit_DEV_for_bob() {
    echo "$1. deposit DEV for Bob"

    cd ${CONFIG_DIR}
    eth transaction:send \
                --network $MOONBEAM_RPC_URL \
                --pk $MOONBEAM_PREFUND_PK \
                --gas $MOONBEAM_GAS_LIMIT \
                --to $(get_bob_address) \
                --value $DEPOSIT_AMOUNT | jq -r > tx.deposit_dev

    eth transaction:get --network $MOONBEAM_RPC_URL $(cat tx.deposit_dev) | jq -r .receipt
    get_bob_balance
}

transfer_DEV_from_bob_to_alice() {
    echo "$1. transfer DEV from Bob to Alice"

    cd ${CONFIG_DIR}
    encoded_data=$(eth method:encode abi.bsh_core.json "transferNativeCoin('$(cat alice.btp.address)')")
    eth transaction:send \
                --network $MOONBEAM_RPC_URL \
                --pk $MOONBEAM_PREFUND_PK \
                --gas $MOONBEAM_GAS_LIMIT \
                --to $(cat bsh_core.moonbeam) \
                --data $encoded_data \
                --value $DEPOSIT_AMOUNT | jq -r > tx.transfer_dev
    eth transaction:get --network $MOONBEAM_RPC_URL $(cat tx.transfer_dev) | jq -r .receipt
}

check_alice_balance_in_Goloop() {
    echo "$1. Checking Alice's balance"
    sleep 10

    cd $CONFIG_DIR
    get_alice_balance
}

echo "This script demonstrates how to transfer a NativeCoin from MOONBEAM to ICON."
create_bob_account_in_Moonbeam  "1"
deposit_DEV_for_bob             "2"
create_alice_account_in_Gochain "3"
transfer_DEV_from_bob_to_alice  "4"
check_alice_balance_in_Goloop   "5"