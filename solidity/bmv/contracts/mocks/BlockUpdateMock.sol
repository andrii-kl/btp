// SPDX-License-Identifier: MIT
pragma solidity >= 0.4.22 < 0.9.0;

import "../libraries/BlockUpdateLib.sol";

contract BlockUpdateMock {

    using BlockUpdateLib for BlockUpdateLib.BlockUpdate;

    function decode(bytes calldata enc) public pure returns (BlockUpdateLib.BlockUpdate memory) {
        return BlockUpdateLib.decode(enc);
    }

}

