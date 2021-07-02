/*
 * Copyright 2021 ICON Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iconloop.btp.nativecoin;

import score.Address;
import score.annotation.Payable;

import java.math.BigInteger;

public interface NCS {

    /**
     * Registers a wrapped token smart contract and id number of a supporting coin.
     * Caller must be the owner
     *
     * {@code _name} It must be listed in the code name registry..
     * The native coin (ICX) must be registered through the constructor.
     *
     * @param _name A coin name.
     */
    void register(String _name);

    /**
     * Return all supported coins names in other networks by the BSH contract
     *
     * @return An array of strings.
     */
    String[] coinNames();

    /**
     * Return an _id number and a symbol of Coin whose name is the same with given _coinName.
     *
     * @implSpec Return nullempty if not found.
     *
     * @param _coinName Coin name.
     * @return id of Native Coin Token.
     */
    BigInteger coinId(String _coinName);

    /**
     * Return a usable/locked balance of an account based on coinName.
     *
     * @implSpec Locked Balance means an amount of Coins/Wrapped Coins is currently
     *          at a pending state when a transfer tx is requested from this chain to another
     * @implSpec Return 0 if not found
     *
     * @param _owner
     * @param _coinName Coin name.
     * @return Balance
     *  {
     *      "locked" : an amount of locked Coins/WrappedCoins,
     *      "refundable" : an amount of refundable Coins/WrappedCoins
     *  }
     */
    Balance balanceOf(Address _owner, String _coinName);

    /**
     * Return a list locked/usable balance of an account.
     *
     * @implSpec The order of request's coinNames must be the same with the order of return balance
     * @implSpec Return 0 if not found.
     *
     * @param _owner
     * @param _coinNames
     * @return Balance[]
     *  [
     *      {
     *          "locked" : an amount of locked Coins/WrappedCoins,
     *          "refundable" : an amount of refundable Coins/WrappedCoins
     *      }
     *  ]
     */
    Balance[] balanceOfBatch(Address _owner, String[] _coinNames);

    /**
     * Reclaim the coin's refundable balance by an owner.
     *
     * @apiNote Caller must be an owner of coin
     * @implSpec This function only applies on native coin (not wrapped coins)
     *        The amount to claim must be smaller than refundable balance
     *
     * @param _coinName A given name of coin to be re-claim
     * @param _value An amount of re-claiming
     */
    void reclaim(String _coinName, BigInteger _value);

    /**
     * Allow users to deposit `msg.value` native coin into a BSH contract.
     *
     * @implSpec MUST specify msg.value
     *
     * @param _to An address that a user expects to receive an equivalent amount of tokens.
     */
    @Payable
    void transferNativeCoin(String _to);

    /**
     * Allow users to deposit an amount of wrapped native coin `_coinName` from the `msg.sender` address into the BSH contract.
     *
     * @apiNote Caller must set to approve that the wrapped tokens can be transferred out of the `msg.sender` account by the operator.
     * @implSpec It MUST revert if the balance of the holder for token `_coinName` is lower than the `_value` sent.
     *
     * @param _coinName A given name of coin that is equivalent to retrieve a wrapped Token Contract's address, i.e. list["Token A"] = 0x12345678
     * @param _value Transferring amount.
     * @param _to Target address.
     */
    void transfer(String _coinName, BigInteger _value, String _to);

    /**
     *  Allows users to deposit `_values` amounts of coins `_coinName` from the `msg.sender` address into the BSH contract.
     *
     *  The caller must set to approve that the wrapped tokens can be transferred out of the `msg.sender` account by the contract. It MUST revert if the balance of the holder for token `_coinName` is lower than the `_value` sent.
     * The order of coinName and value should be matched
     *
     *  @param _coinNames    Given names of each coin that is equivalent to retrieve a wrapped Token Contract's address, i.e. list["Token A"] = 0x12345678
     *  @param _values       Transferring amounts per coin
     *  @param _to          Target BTP Address.
     */
    void transferBatch(String[] _coinNames, BigInteger[] _values, String _to);

    /**
     * FIXME
     *
     * @param _feeRate
     */
    void setFeeRate(String _feeRate);

    /**
     * FIXME
     *
     * @return
     */
    String getFeeRate();
}
