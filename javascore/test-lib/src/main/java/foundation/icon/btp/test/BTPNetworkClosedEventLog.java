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

package foundation.icon.btp.test;

import foundation.icon.btp.mock.ChainScore;
import foundation.icon.jsonrpc.IconJsonModule;
import foundation.icon.jsonrpc.model.TransactionResult;
import foundation.icon.score.test.ScoreIntegrationTest;

import java.util.List;

public class BTPNetworkClosedEventLog {
    public static final String SIGNATURE = "BTPNetworkClosed(int,int)";
    private long networkTypeId;
    private long networkId;

    public long getNetworkTypeId() {
        return networkTypeId;
    }

    public long getNetworkId() {
        return networkId;
    }

    public BTPNetworkClosedEventLog(TransactionResult.EventLog el) {
        networkTypeId = IconJsonModule.NumberDeserializer.LONG.convert(el.getIndexed().get(1));
        networkId = IconJsonModule.NumberDeserializer.LONG.convert(el.getIndexed().get(2));
    }

    public static List<BTPNetworkClosedEventLog> eventLogs(
            TransactionResult txr) {
        return ScoreIntegrationTest.eventLogs(txr,
                BTPNetworkClosedEventLog.SIGNATURE,
                new foundation.icon.jsonrpc.Address(ChainScore.ADDRESS.toString()),
                BTPNetworkClosedEventLog::new,
                null);
    }
}
