/*
 * Copyright 2022 ICON Foundation
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

package foundation.icon.score.test;

import foundation.icon.jsonrpc.IconJsonModule;
import foundation.icon.jsonrpc.model.TransactionResult;
import score.Address;

import java.math.BigInteger;
import java.util.List;
import java.util.function.Predicate;

public class ICXTransferEventLog {
    static final String SIGNATURE = "ICXTransfer(Address,Address,int)";
    private Address from;
    private Address to;
    private BigInteger amount;

    public ICXTransferEventLog(TransactionResult.EventLog el) {
        this.from = IconJsonModule.AddressDeserializer.SCORE_ADDRESS.convert(el.getIndexed().get(1));
        this.to = IconJsonModule.AddressDeserializer.SCORE_ADDRESS.convert(el.getIndexed().get(2));
        this.amount = IconJsonModule.NumberDeserializer.BIG_INTEGER.convert(el.getIndexed().get(3));
    }

    public Address getFrom() {
        return from;
    }

    public Address getTo() {
        return to;
    }

    public BigInteger getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ICXTransferEventLog{");
        sb.append("from=").append(from);
        sb.append(", to=").append(to);
        sb.append(", amount=").append(amount);
        sb.append('}');
        return sb.toString();
    }

    public static List<ICXTransferEventLog> eventLogs(
            TransactionResult txr, foundation.icon.jsonrpc.Address address, Predicate<ICXTransferEventLog> filter) {
        return ScoreIntegrationTest.eventLogs(txr,
                ICXTransferEventLog.SIGNATURE,
                address,
                ICXTransferEventLog::new,
                filter);
    }
}
