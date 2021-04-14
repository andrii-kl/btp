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
package foundation.icon.btp.bmv;

import score.Context;
import score.ObjectReader;

import scorex.util.ArrayList;

public class RelayMessage {

    final static String RLPn = "RLPn";

    private ArrayList<BlockUpdate> blockUpdates;
    private ArrayList<ReceiptProof> receiptProofs;
    private BlockProof blockProof;

    public RelayMessage(BlockProof blockProof,
                        ArrayList<BlockUpdate> blockUpdates,
                        ArrayList<ReceiptProof> receiptProof) {
        this.blockUpdates = blockUpdates;
        this.blockProof = blockProof;
        this.receiptProofs = receiptProof;
    }

    public static RelayMessage fromBytes(byte[] bytes) {
        ObjectReader reader = Context.newByteArrayObjectReader(RLPn, bytes);
        reader.beginList();

        reader.beginList();
        var blockUpdates = new ArrayList<BlockUpdate>();
        while(reader.hasNext())
            blockUpdates.add(BlockUpdate.fromBytes(reader.readByteArray()));
        reader.end();

        BlockProof blockProof = BlockProof.fromBytes(reader.readNullable(byte[].class));

        reader.beginList();
        var receiptProofs = new ArrayList<ReceiptProof>();
        while(reader.hasNext())
            receiptProofs.add(ReceiptProof.fromBytes(reader.readByteArray()));
        reader.end();

        return new RelayMessage(
                blockProof,
                blockUpdates,
                receiptProofs);
    }

    public BlockUpdate[] getBlockUpdates() {
        BlockUpdate[] tmp = new BlockUpdate[blockUpdates.size()];
        for (int i = 0; i < tmp.length; i++) {
            tmp[i] = blockUpdates.get(i);
        }
        return tmp;
    }

    public ReceiptProof[] getReceiptProofs() {
        ReceiptProof[] tmp = new ReceiptProof[receiptProofs.size()];
        for (int i = 0; i < tmp.length; i++) {
            tmp[i] = receiptProofs.get(i);
        }
        return tmp;
    }

    public BlockProof getBlockProof() {
        return blockProof;
    }
}