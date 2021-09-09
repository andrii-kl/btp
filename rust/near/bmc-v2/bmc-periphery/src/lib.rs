//! BMCPeriphery Contract

use near_sdk::ext_contract;

#[ext_contract(ibsh)]
pub trait Ibsh {
    /**
       @notice BSH handle BTP Message from BMC contract
       @dev Caller must be BMC contract only
       @param from    An originated network address of a request
       @param svc     A service name of BSH contract
       @param sn      A serial number of a service request
       @param msg     An RLP message of a service request/service response
    */
    fn handle_btp_message(&mut self, from: AccountId, svc: String, sn: u128, msg: Vec<u8>);

    /**
       @notice BSH handle BTP Error from BMC contract
       @dev Caller must be BMC contract only
       @param svc     A service name of BSH contract
       @param sn      A serial number of a service request
       @param code    A response code of a message (RC_OK / RC_ERR)
       @param msg     A response message
    */
    fn handle_btp_error(&mut self, src: String, svc: String, sn: u128, code: u32, msg: String);

    /**
       @notice BSH handle Gather Fee Message request from BMC contract
       @dev Caller must be BMC contract only
       @param fa     A BTP address of fee aggregator
       @param svc    A name of the service
    */
    fn handle_fee_gathering(&mut self, fa: AccountId, svc: String);
}

#[ext_contract(ibmv)]
pub trait Ibmv {
    /**
       @return        Base64 encode of Merkle Tree
    */
    fn get_mta(&self) -> String;

    /**
       @return        Connected BMC address
    */
    fn get_connected_bmc(&self) -> AccountId;

    /**
       @return        Network address of the blockchain
    */
    fn get_net_address(&self) -> AccountId;

    /**
       @return        Hash of RLP encode from given list of validators
       @return        List of validators' addresses
    */
    fn get_validators(&self) -> (CryptoHash, Vec<AccountId>);

    /**
       @notice Used by the relay to resolve next BTP Message to send.
               Called by BMC.
       @return        Height of MerkleTreeAccumulator
       @return        Offset of MerkleTreeAccumulator
       @return        Block height of last relayed BTP Message
    */
    fn get_status(&self) -> (u128, usize, u128);

    /**
       @notice Decodes Relay Messages and process BTP Messages.
               If there is an error, then it sends a BTP Message containing the Error Message.
               BTP Messages with old sequence numbers are ignored. A BTP Message contains future sequence number will fail.
       @param bmc     BTP Address of the BMC handling the message
       @param prev    BTP Address of the previous BMC
       @param seq     Next sequence number to get a message
       @param msg     Serialized bytes of Relay Message
       @return        List of serialized bytes of a BTP Message
    */
    fn handle_relay_message(
        &mut self,
        bmc: AccountId,
        prev: AccountId,
        seq: u128,
        msg: String,
    ) -> Vec<Vec<u8>>;
}

#[ext_contract(ibmc_periphery)]
pub trait IbmcPeriphery {
    /**
       @notice Get BMC BTP address
    */
    fn get_bmc_btp_address(&self) -> AccountId;

    /**
       @notice Verify and decode RelayMessage with BMV, and dispatch BTP Messages to registered BSHs
       @dev Caller must be a registered relayer.
       @param prev    BTP Address of the BMC generates the message
       @param msg     base64 encoded string of serialized bytes of Relay Message refer RelayMessage structure
    */
    fn handle_relay_message(&mut self, prev: AccountId, msg: String);

    /**
       @notice Send the message to a specific network.
       @dev Caller must be a registered BSH.
       @param to      Network Address of destination network
       @param svc     Name of the service
       @param sn      Serial number of the message, it should be positive
       @param msg     Serialized bytes of Service Message
    */
    fn send_message(&mut self, to: AccountId, svc: String, sn: u64, msg: Vec<u8>);

    /**
       @notice Get status of BMC.
       @param link    BTP Address of the connected BMC
       @return        The link status
    */
    fn get_status(&self, link: AccountId) -> LinkStats;
}

#[ext_contract(ibmc_management)]
pub trait IbmcManagement {
    /**
      @notice Update BMC periphery.
      @dev Caller must be an Onwer of BTP network
      @param addr    Address of a new periphery.
    */
    fn set_bmc_periphery(&mut self, addr: AccountId);

    /**
      @notice Adding another Onwer.
      @dev Caller must be an Onwer of BTP network
      @param owner    Address of a new Onwer.
    */
    fn add_owner(&mut self, owner: AccountId);

    /**
      @notice Removing an existing Owner.
      @dev Caller must be an Owner of BTP network
      @dev If only one Owner left, unable to remove the last Owner
      @param owner    Address of an Owner to be removed.
    */
    fn remove_owner(&mut self, owner: AccountId);

    /**
      @notice Checking whether one specific address has Owner role.
      @dev Caller can be ANY
      @param owner    Address needs to verify.
    */
    fn is_owner(&self, owner: AccountId) -> bool;

    /**
      @notice Add the smart contract for the service.
      @dev Caller must be an operator of BTP network.
      @param svc     Name of the service
      @param addr    Service's contract address
    */
    fn add_service(&mut self, svc: String, addr: AccountId);

    /**
      @notice De-registers the smart contract for the service.
      @dev Caller must be an operator of BTP network.
      @param svc     Name of the service
    */
    fn remove_service(&mut self, svc: String);

    /**
      @notice Get registered services.
      @return services   An array of Service.
    */
    fn get_services(&self) -> Vec<Service>;

    /**
      @notice Registers BMV for the network.
      @dev Caller must be an operator of BTP network.
      @param net     Network Address of the blockchain
      @param addr    Address of BMV
    */
    fn add_verifier(&mut self, net: AccountId, addr: AccountId);

    /**
      @notice De-registers BMV for the network.
      @dev Caller must be an operator of BTP network.
      @param net     Network Address of the blockchain
    */
    fn remove_verifier(&mut self, net: AccountId);

    /**
      @notice Get registered verifiers.
      @return verifiers   An array of Verifier.
    */
    fn get_verifiers(&self) -> Vec<Verifier>;

    /**
      @notice Initializes status information for the link.
      @dev Caller must be an operator of BTP network.
      @param link    BTP Address of connected BMC
    */
    fn add_link(&mut self, link: AccountId);

    /**
      @notice Removes the link and status information.
      @dev Caller must be an operator of BTP network.
      @param link    BTP Address of connected BMC
    */
    fn remove_link(&mut self, link: AccountId);

    /**
      @notice Get registered links.
      @return   An array of links ( BTP Addresses of the BMCs ).
    */
    fn get_links(&self) -> &Vec<String>;

    /**
      @notice Set the link and status information.
      @dev Caller must be an operator of BTP network.
      @param link    BTP Address of connected BMC
      @param block_interval    Block interval of a connected link
      @param max_aggregation   Set max aggreation of a connected link
      @param delay_limit       Set delay limit of a connected link
    */
    fn set_link(&mut self, link: AccountId, block_interval: u128, max_agg: u128, delay_limit: u128);

    /**
       @notice rotate relay for relay address. Only called by BMC periphery.
       @param link               BTP network address of connected BMC
       @param current_height     current block height of MTA from BMV
       @param relay_msg_height   block height of last relayed BTP Message
       @param hasMsg             check if message exists
       @return                   relay address
    */
    fn rotate_relay(
        &mut self,
        link: AccountId,
        current_height: u128,
        relay_msg_height: u128,
        has_msg: bool,
    ) -> String;

    /**
      @notice Add route to the BMC.
      @dev Caller must be an operator of BTP network.
      @param dst     BTP Address of the destination BMC
      @param link    BTP Address of the next BMC for the destination
    */
    fn add_route(&mut self, dst: AccountId, link: AccountId);

    /**
      @notice Remove route to the BMC.
      @dev Caller must be an operator of BTP network.
      @param dst     BTP Address of the destination BMC
    */
    fn remove_route(&mut self, dst: AccountId);

    /**
      @notice Get routing information.
      @return An array of Route.
    */
    fn get_routes(&self) -> Vec<Route>;

    /**
      @notice Registers relay for the network.
      @dev Caller must be an operator of BTP network.
      @param link      BTP Address of connected BMC
      @param addrs     A list of Relays
    */
    fn add_relay(&mut self, link: AccountId, addrs: Vec<AccountId>);

    /**
      @notice Unregisters Relay for the network.
      @dev Caller must be an operator of BTP network.
      @param link      BTP Address of connected BMC
      @param addrs     A list of Relays
    */
    fn remove_relay(&mut self, link: AccountId, addr: AccountId);

    /**
      @notice Get registered relays.
      @param link        BTP Address of the connected BMC.
      @return            A list of relays.
    */
    fn get_relays(&self, link: AccountId) -> Vec<AccountId>;

    /**
       @notice Get BSH services by name. Only called by BMC periphery.
       @param service_name  BSH service name
       @return              BSH service address
    */
    fn get_bsh_service_by_name(&self, service_name: String) -> String;

    /**
       @notice Get BMV services by net. Only called by BMC periphery.
       @param net       net of the connected network
       @return          BMV service address
    */
    fn get_bmv_service_by_net(&self, net: AccountId) -> String;

    /**
       @notice Get link info. Only called by BMC periphery.
       @param to     link's BTP address
       @return       Link info
    */
    fn get_link(&self, to: AccountId) -> Link;

    /**
       @notice Get rotation sequence by link. Only called by BMC periphery.
       @param prev     BTP Address of the previous BMC
       @return         Rotation sequence
    */
    fn get_link_rx_seq(&self, prev: AccountId) -> u128;

    /**
       @notice Get transaction sequence by link. Only called by BMC periphery.
       @param prev    BTP Address of the previous BMC
       @return        Transaction sequence
    */
    fn get_link_tx_seq(&self, prev: AccountId) -> u128;

    /**
       @notice Get relays by link. Only called by BMC periphery.
       @param prev    BTP Address of the previous BMC
       @return        List of relays' addresses
    */
    fn get_link_relays(&self, prev: AccountId) -> Vec<AccountId>;

    /**
       @notice Get relays status by link. Only called by BMC periphery.
       @param prev    BTP Address of the previous BMC
       @return        Relay status of all relays
    */
    fn get_relay_status_by_link(&self, prev: AccountId) -> Vec<RelayStats>;

    /**
       @notice Update rotation sequence by link. Only called by BMC periphery.
       @param prev    BTP Address of the previous BMC
       @param val     increment value
    */
    fn update_link_rx_seq(&mut self, prev: AccountId, val: u128);

    /**
       @notice Increase transaction sequence by 1.
       @param prev    BTP Address of the previous BMC
    */
    fn update_link_tx_seq(&mut self, prev: AccountId);

    /**
       @notice Add a reachable BTP address to link. Only called by BMC periphery.
       @param prev   BTP Address of the previous BMC
       @param to     BTP Address of the reachable
    */
    fn update_link_reachable(&mut self, prev: AccountId, to: Vec<AccountId>);

    /**
       @notice Remove a reachable BTP address. Only called by BMC periphery.
       @param index   reachable index to remove
    */
    fn delete_link_reachable(&mut self, prev: AccountId, index: usize);

    /**
       @notice Update relay status. Only called by BMC periphery.
       @param relay                relay address
       @param block_count_val      increment value for block counter
       @param msg_count_val        increment value for message counter
    */
    fn update_relay_stats(
        &mut self,
        relay: AccountId,
        block_count_val: u128,
        msg_count_val: u128,
    ) -> Result<(), &str>;

    /**
       @notice resolve next BMC. Only called by BMC periphery.
       @param dst_net     net of BTP network address
       @return            BTP address of next BMC and destinated BMC
    */
    fn resolve_route(&mut self, dst_net: AccountId) -> (String, String);
}
