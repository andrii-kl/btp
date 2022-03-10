use std::convert::TryFrom;

use super::*;
use libraries::types::{
    messages::{
        BmcServiceMessage, BmcServiceType, BtpMessage, SerializedBtpMessages, SerializedMessage,
        TokenServiceMessage, TokenServiceType,
    },
    Account, AccountBalance, AccumulatedAssetFees, Asset, BTPAddress, Math, TransferableAsset,
    VerifierResponse, VerifierStatus, WrappedI128, WrappedNativeCoin,
};
use serde_json::json;
use test_helper::types::Context;

pub static USER_INVOKES_HANDLE_RELAY_MESSAGE_IN_BMC: fn(Context) -> Context =
    |context: Context| BMC_CONTRACT.handle_relay_message(context, 300_000_000_000_000);

pub static RELAY_1_INVOKES_HANDLE_RELAY_MESSAGE_IN_BMC: fn(Context) -> Context =
    |context: Context| {
        context
            .pipe(THE_TRANSACTION_IS_SIGNED_BY_RELAY_1)
            .pipe(USER_INVOKES_HANDLE_RELAY_MESSAGE_IN_BMC)
    };

pub static BMC_INIT_LINK_RELAY_MESSAGE_IS_PROVIDED_AS_HANDLE_RELAY_MESSAGE_PARAM: fn(
    Context,
) -> Context = |mut context: Context| {
    // let link =
    //     BTPAddress::new("btp://0x1.icon/cx87ed9048b594b95199f326fc76e76a9d33dd665b".to_string());

    let bmc_service_message = BmcServiceMessage::new(BmcServiceType::Init {
        links: vec![
            BTPAddress::new("btp://0x1.pra/cx87ed9048b594b95199f326fc76e76a9d33dd665b".to_string()),
            BTPAddress::new("btp://0x5.pra/cx87ed9048b594b95199f326fc76e76a9d33dd665b".to_string()),
        ],
    });
    let btp_message = <BtpMessage<SerializedMessage>>::new(
        BTPAddress::new("btp://0x1.icon/cx87ed9048b594b95199f326fc76e76a9d33dd665b".to_string()),
        BTPAddress::new(format!(
            "btp://0x1.near/{}",
            context.contracts().get("bmc").id()
        )),
        "bmc".to_string(),
        WrappedI128::new(1),
        <Vec<u8>>::from(bmc_service_message.clone()),
        None,
    );

    let verifier_response = VerifierResponse {
        messages: vec![btp_message],
        verifier_status: VerifierStatus::new(84644028, 0, 84644028),
        previous_height: 84644027,
    };

    context.add_method_params(
        "handle_relay_message_bmv_callback_mockable",
        json!({
            "source": format!("btp://{}/{}", ICON_NETWORK, ICON_BMC),
            "verifier_response": verifier_response,
            "relay": context.accounts().get("relay_1").id()
        }),
    );
    context
};

pub static BMC_INIT_LINK_RELAY_MESSAGE_IS_PROVIDED_AS_HANDLE_RELAY_MESSAGE_PARAM_FOR_NON_EXISTING_LINK: fn(Context) -> Context = |mut context: Context| {
    context.add_method_params(
        "handle_relay_message",
        json!({
            "source": format!("btp://{}/{}", NEAR_NETWORK, ICON_BMC),
            "message": "-Qee-QQ8uQFS-QFPuNT40gKCQKOHBdCqJx0eQ5UAtrV5G-C172cGOzwQuED7gVFNsv2g1tINkHhbtViQXPruQ-FnkmYs_jA0tYrJKbvq3BYO9Rqgt12iGPeeEGlPxvT_hNLnhr9K5nUqvKiB6A_Yc0bfl76g7Z5kTlmy_2VEb189fXfCeFj6z4rrO5aUcNdJnHn5dXz4APgAgLhG-ESgMpG4jtB7-p0Ls3k2EYmLp9RerPVrvKAFveS23xQl2UT4AKBhUsXohogqHXqYEfjsfRAHCiSbbzQfM_H7NFRDIoxzKLh1-HMA4gGgtVIyurXEb0cIjT3frYFFTMfDQ48iwgntObJ1ht6v-RL4TfhLhwXQqicsVs-4QfNbBjk7e16wLiu54BjoVhrFrH41obLbpuP8tVCxRbVLHYoBQyGbXYOYQduPi9KL_jc9iJ284R8Z5uKMJYJvntwB-AC5AVD5AU240vjQAoJApIcF0KonLFbPlQC2tXkb4LXvZwY7PBC4QPuBUU2y_aB_WlGDMMOOnc25Zw71Asy9qm5P1ckuxut4n7eyRa8sX6BkLQ2BfmajCXpSPKQyPbQfknSBaVE5Jh3D-Wyd1gODu6DtnmROWbL_ZURvXz19d8J4WPrPius7lpRw10mcefl1fPgAoNBv8fj4bmAFfa9a8IyYHOK_pHPZt7lJi8AF0Cgkad7_gKbloDKRuI7Qe_qdC7N5NhGJi6fUXqz1a7ygBb3ktt8UJdlE-AD4ALh1-HMA4gGgqw1QRCuG0tcqzCpKDqhHQO09T3_OQMhOHAAgt-D4O-_4TfhLhwXQqic7qGy4QZPydQXTosV0Bw1Q3ntf7-FCxRDMSM7FOnPhP591OxiYUlsWMdK0qfbp-WrnfBJKObfEwNm1dqfX98D9xKLtCVsA-AC5AZH5AY65ARL5AQ8CgkClhwXQqic7qGyVALa1eRvgte9nBjs8ELhA-4FRTbL9oItWxVWVSnoGHZaeON6tuc1mKVrq1Wm18UkpjaplJALMoBbHRfvDVLJUkLGrwX2BHDYbXjXpRT-EcCLGG68iaSdKoO2eZE5Zsv9lRG9fPX13wnhY-s-K6zuWlHDXSZx5-XV8-AD4ALg8CAAgcEAMEg8IhMDEBAhUDg0OhQEgYIiMEEEWjMIisaAENjsEAUIjEXghCkEZQEbjMQhEChETlEglUKgIuEb4RKC_bo8qyH3l705OLCmJ1kMQd9iEV0aw7gStxMPmCTOAffgAoL2i-AmVd_b_p3gkSP5glYl9pIn40AEUMIvqwpHIvkahuHX4cwDiAaCp1buI8W7RsDhZAp69_T6gJBjO_aR00EmjIj3-397EXfhN-EuHBdCqJ0rbgLhB459yJ9MK7TDZ2xQ23jTHbdbiwOCTEcmb5U7olxiUgaFV28sh8nuVzXrjJXuGfUnNureSKSOSXSwvp-aQTixLNAH4APgA-QNauQNX-QNUALkBVPkBUbkBTvkBS4IgALkBRfkBQgCVAZQ5KOt2a33MGDNmJIyCIn-oz8dWgwdSZIMHUmSFAukO3QC49hAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAAAgQAAAAAAAAAAAAAEAAAAAAAAAAAAAAAAAAAAABAAACAAAAAAAAAAAAAAAACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAAAAAAAAAAAAAAAAAAEAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAACAAAAAAIAAAAEIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAAAAAAACAAAAAAAAAAAAAAAAQAAAAAAAAAQAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAPgA-ACgvEWAgjiMdu0pUAxvQcurR0OVNFEmP5ZzhNMkpIVwqPr5Afn5AfYAuQHy-QHvo-IQoMOetu_ZC-YK5vgAe7UsNeBouviaGeP9VQgl1JDY7dRguFP4UaBjTIu0WBCx1UEf8MBqzibUyAkFNsmaQ3gnewIx7LIl96CegRcYwno7-6NZanIbuiC4o_93OVbLE-UByERTjAHFv4CAgICAgICAgICAgICAgLkBc_kBcCC5AWz5AWmVAZwHK-9UWr65aj97ov_fi4YftV3q-FSWTWVzc2FnZShzdHIsaW50LGJ5dGVzKbg6YnRwOi8vMHg1MDEucHJhLzB4NUNDMzA3MjY4YTEzOTNBQjlBNzY0QTIwREFDRTg0OEFCODI3NWM0NgL4-7j5-Pe4PmJ0cDovLzB4NThlYjFjLmljb24vY3g5YzA3MmJlZjU0NWFiZWI5NmEzZjdiYTJmZmRmOGI4NjFmYjU1ZGVhuDpidHA6Ly8weDUwMS5wcmEvMHg1Q0MzMDcyNjhhMTM5M0FCOUE3NjRBMjBEQUNFODQ4QUI4Mjc1YzQ2im5hdGl2ZWNvaW4BuG34awC4aPhmqmh4NDUwMmFhZDc5ODZhZDVhODQ4OTU1MTVmYWY3NmU5MGI1YjQ3ODY1NKoweDE1OEEzOTFGMzUwMEMzMjg4QWIyODY1MzcyMmE2NDU5RTc3MjZCMDHPzoNJQ1iJAIlj3YwsXgAA"
        }),
    );
    context
};

pub static BMC_SHOULD_THROW_LINK_DOES_NOT_EXIST_ERROR_ON_HANDLING_RELAY_MESSAGES: fn(Context) =
    |context: Context| {
        let error = context.method_errors("handle_relay_message");
        assert!(error.to_string().contains("BMCRevertNotExistsLink"));
    };

pub static RELAY_2_INVOKES_HANDLE_RELAY_MESSAGE_IN_BMC: fn(Context) -> Context =
    |context: Context| {
        context
            .pipe(RELAY_2_ACCOUNT_IS_CREATED)
            .pipe(THE_TRANSACTION_IS_SIGNED_BY_RELAY_2)
            .pipe(USER_INVOKES_HANDLE_RELAY_MESSAGE_IN_BMC)
    };

pub static ALICE_INVOKES_HANDLE_INIT_LINK_MESSAGE_IN_BMC: fn(Context) -> Context =
    |mut context: Context| {
        context
            .pipe(THE_TRANSACTION_IS_SIGNED_BY_ALICE)
            .pipe(USER_INVOKES_HANDLE_RELAY_MESSAGE_BMV_CALLBACK_IN_BMC)
    };

pub static ALICE_INVOKES_HANDLE_BTP_MESSAGE_IN_BMC: fn(Context) -> Context =
    |mut context: Context| {
        context
            .pipe(THE_TRANSACTION_IS_SIGNED_BY_ALICE)
            .pipe(USER_INVOKES_HANDLE_RELAY_MESSAGE_BMV_CALLBACK_IN_BMC)
    };

pub static UNREGISTERED_BSH_RECEIVES_RESPONSE_HANDLE_BTP_MESSAGE_TO_NATIVE_COIN: fn(
    Context,
) -> Context = |mut context: Context| {
    let destination =
        BTPAddress::new("btp://0x1.icon/cx87ed9048b594b95199f326fc76e76a9d33dd665b".to_string());
    let btp_message = &BtpMessage::new(
        BTPAddress::new("btp://0x1.icon/0x12345678".to_string()),
        BTPAddress::new("btp://1234.iconee/0x12345678".to_string()),
        "nativecoin_2".to_string(),
        WrappedI128::new(1),
        vec![],
        Some(TokenServiceMessage::new(
            TokenServiceType::ResponseHandleService {
                code: 0,
                message: "Transfer Success".to_string(),
            },
        )),
    );

    let serialized_message = BtpMessage::try_from(btp_message).unwrap();

    let verifier_response = VerifierResponse {
        messages: vec![serialized_message],
        verifier_status: VerifierStatus::new(10, 10, 10),
        previous_height: 10,
    };

    context.add_method_params(
        "handle_relay_message_bmv_callback_mockable",
        json!({
            "source": format!("btp://{}/{}", ICON_NETWORK, ICON_BMC),
            "verifier_response": verifier_response,
        }),
    );
    context
};

pub static UNREGISTERED_BSH_RECEIVES_BTP_MESSAGE_TO_MINT_AND_TRANSFER_WRAPPED_NATIVE_COIN:
    fn(Context) -> Context = |mut context: Context| {
    let destination =
        BTPAddress::new("btp://0x1.icon/cx87ed9048b594b95199f326fc76e76a9d33dd665b".to_string());
    let btp_message = &BtpMessage::new(
        BTPAddress::new("btp://0x1.icon/0x12345678".to_string()),
        BTPAddress::new("btp://1234.iconee/0x12345678".to_string()),
        "nativecoin_2".to_string(),
        WrappedI128::new(1),
        vec![],
        Some(TokenServiceMessage::new(
            TokenServiceType::RequestTokenTransfer {
                sender: destination.account_id().to_string(),
                receiver: context.accounts().get("charlie").id().to_string(),
                assets: vec![TransferableAsset::new("WrappedICX".to_string(), 900, 99)],
            },
        )),
    );

    let serialized_message = BtpMessage::try_from(btp_message).unwrap();

    let verifier_response = VerifierResponse {
        messages: vec![serialized_message],
        verifier_status: VerifierStatus::new(84644028, 0, 84644028),
        previous_height: 84644027,
    };
    context.add_method_params(
        "handle_relay_message_bmv_callback_mockable",
        json!({
        "source": format!("btp://{}/{}", ICON_NETWORK, ICON_BMC),
        "verifier_response": verifier_response,
        "relay": context.accounts().get("relay_1").id()
        }),
    );
    context
};

pub static BMC_SENDS_BTP_MESSAGE_TO_MINT_AND_TRANSFER_IN_WRAPPED_NATIVE_COIN: fn(
    Context,
) -> Context = |mut context: Context| {
    let destination = BTPAddress::new(format!(
        "btp://0x1.near/{}",
        context.contracts().get("bmc").id()
    ));
    let btp_message = &BtpMessage::new(
        BTPAddress::new("btp://0x1.icon/0x12345678".to_string()),
        destination,
        "nativecoin".to_string(),
        WrappedI128::new(1),
        vec![],
        Some(TokenServiceMessage::new(
            TokenServiceType::RequestTokenTransfer {
                sender: "0x12345678".to_string(),
                receiver: context.accounts().get("charlie").id().to_string(),
                assets: vec![TransferableAsset::new("WrappedICX".to_string(), 900, 99)],
            },
        )),
    );

    let serialized_message = BtpMessage::try_from(btp_message).unwrap();

    let verifier_response = VerifierResponse {
        verifier_status: VerifierStatus::new(1846542, 1, 1846539),
        previous_height: 1846538,
        messages: vec![serialized_message],
    };
    context.add_method_params(
        "handle_relay_message_bmv_callback_mockable",
        json!({
        "source": format!("btp://{}/{}", ICON_NETWORK, ICON_BMC),
        "verifier_response": verifier_response,
        "relay": context.accounts().get("relay_1").id()
        }),
    );
    context
};

pub static BMC_LINK_MESSAGE_IS_PROVIDED_AS_HANDLE_RELAY_MESSAGE_PARAM: fn(Context) -> Context =
    |mut context: Context| {
        // let link =
        //     BTPAddress::new("btp://0x1.icon/cx87ed9048b594b95199f326fc76e76a9d33dd665b".to_string());

        let bmc_service_message = BmcServiceMessage::new(BmcServiceType::Link {
            link: BTPAddress::new(
                "btp://0x1.pra/cx87ed9048b594b95199f326fc76e76a9d33dd665b".to_string(),
            ),
        });
        let btp_message = <BtpMessage<SerializedMessage>>::new(
            BTPAddress::new(
                "btp://0x1.icon/cx87ed9048b594b95199f326fc76e76a9d33dd665b".to_string(),
            ),
            BTPAddress::new(format!(
                "btp://0x1.near/{}",
                context.contracts().get("bmc").id()
            )),
            "bmc".to_string(),
            WrappedI128::new(1),
            <Vec<u8>>::from(bmc_service_message.clone()),
            None,
        );

        let verifier_response = VerifierResponse {
            messages: vec![btp_message],
            verifier_status: VerifierStatus::new(84644028, 0, 84644028),
            previous_height: 84644027,
        };

        context.add_method_params(
            "handle_relay_message_bmv_callback_mockable",
            json!({
                "source": format!("btp://{}/{}", ICON_NETWORK, ICON_BMC),
                "verifier_response": verifier_response,
                "relay": context.accounts().get("relay_1").id()
            }),
        );
        context
    };

pub static BMC_UNLINK_MESSAGE_IS_PROVIDED_AS_HANDLE_RELAY_MESSAGE_PARAM: fn(Context) -> Context =
    |mut context: Context| {
        // let link =
        //     BTPAddress::new("btp://0x1.icon/cx87ed9048b594b95199f326fc76e76a9d33dd665b".to_string());

        let bmc_service_message = BmcServiceMessage::new(BmcServiceType::Unlink {
            link: BTPAddress::new(
                "btp://0x1.pra/cx87ed9048b594b95199f326fc76e76a9d33dd665b".to_string(),
            ),
        });
        let btp_message = <BtpMessage<SerializedMessage>>::new(
            BTPAddress::new(
                "btp://0x1.icon/cx87ed9048b594b95199f326fc76e76a9d33dd665b".to_string(),
            ),
            BTPAddress::new(format!(
                "btp://0x1.near/{}",
                context.contracts().get("bmc").id()
            )),
            "bmc".to_string(),
            WrappedI128::new(1),
            <Vec<u8>>::from(bmc_service_message.clone()),
            None,
        );

        let verifier_response = VerifierResponse {
            messages: vec![btp_message],
            verifier_status: VerifierStatus::new(84644028, 0, 84644028),
            previous_height: 84644027,
        };

        context.add_method_params(
            "handle_relay_message_bmv_callback_mockable",
            json!({
                "source": format!("btp://{}/{}", ICON_NETWORK, ICON_BMC),
                "verifier_response": verifier_response,
                "relay": context.accounts().get("relay_1").id()
            }),
        );
        context
    };

pub static ALICE_INVOKES_HANDLE_RELAY_MESSAGE_BMV_CALLBACK_IN_BMC: fn(Context) -> Context =
    |mut context: Context| {
        context
            .pipe(THE_TRANSACTION_IS_SIGNED_BY_ALICE)
            .pipe(USER_INVOKES_HANDLE_RELAY_MESSAGE_BMV_CALLBACK_IN_BMC)
    };

pub static BMC_SHOULD_RECIEVE_THE_TRANSACTION_RESPONCE_MESSAGE_FROM_BSH: fn(Context) =
    |mut context: Context| {
        let context = context.pipe(USER_INVOKES_GET_MESSAGES_IN_BMC);
        let message: Result<Result<BtpMessage<SerializedMessage>, String>, serde_json::Error> =
            serde_json::from_value(context.method_responses("get_message"));
        let service_message =
            <BtpMessage<TokenServiceMessage>>::try_from(message.unwrap().unwrap());

        let expected_message = &BtpMessage::new(
            BTPAddress::new(format!(
                "btp://0x1.near/{}",
                context.contracts().get("bmc").id()
            )),
            BTPAddress::new(
                "btp://0x1.icon/0xc294b1A62E82d3f135A8F9b2f9cAEAA23fbD6Cf5".to_string(),
            ),
            "nativecoin".to_string(),
            WrappedI128::new(1),
            vec![
                213, 2, 147, 210, 128, 144, 84, 114, 97, 110, 115, 102, 101, 114, 32, 83, 117, 99,
                99, 101, 115, 115,
            ],
            Some(TokenServiceMessage::new(
                TokenServiceType::ResponseHandleService {
                    code: 0,
                    message: "Transfer Success".to_string(),
                },
            )),
        );

        assert_eq!(service_message.unwrap(), expected_message.to_owned());
    };

pub static BSH_SHOULD_RECIEVE_INVALID_BSH_MESSAGE_FROM_BMC: fn(Context) = |mut context: Context| {
    let context = context.pipe(USER_INVOKES_GET_MESSAGES_IN_BMC);
    let message: Result<Result<BtpMessage<SerializedMessage>, String>, serde_json::Error> =
        serde_json::from_value(context.method_responses("get_message"));
    let service_message = <BtpMessage<TokenServiceMessage>>::try_from(message.unwrap().unwrap());

    let expected_message = &BtpMessage::new(
        BTPAddress::new(format!(
            "btp://0x1.near/{}",
            context.contracts().get("bmc").id()
        )),
        BTPAddress::new("btp://0x1.icon/0x12345678".to_string()),
        "nativecoin_2".to_string(),
        WrappedI128::new(-1),
        vec![
            246, 21, 180, 66, 77, 67, 82, 101, 118, 101, 114, 116, 85, 110, 114, 101, 97, 99, 104,
            97, 98, 108, 101, 32, 97, 116, 32, 98, 116, 112, 58, 47, 47, 49, 50, 51, 52, 46, 105,
            99, 111, 110, 101, 101, 47, 48, 120, 49, 50, 51, 52, 53, 54, 55, 56,
        ],
        Some(TokenServiceMessage::new(TokenServiceType::UnhandledType)),
    );

    assert_eq!(service_message.unwrap(), expected_message.to_owned());
};
