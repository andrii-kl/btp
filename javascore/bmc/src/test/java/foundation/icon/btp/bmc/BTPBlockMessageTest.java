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

package foundation.icon.btp.bmc;

import foundation.icon.btp.lib.BTPAddress;
import foundation.icon.btp.test.BTPIntegrationTest;
import foundation.icon.btp.test.MockBMVIntegrationTest;
import foundation.icon.btp.test.MockBSHIntegrationTest;
import foundation.icon.btp.test.MockGovIntegrationTest;
import foundation.icon.jsonrpc.Address;
import foundation.icon.jsonrpc.model.TransactionResult;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BTPBlockMessageTest implements BTPBlockIntegrationTest {
    static BTPAddress linkBtpAddress = BTPIntegrationTest.Faker.btpLink();
    static String link = linkBtpAddress.toString();
    static String net = linkBtpAddress.net();
    static Address relay = Address.of(bmc._wallet());
    static String svc = MockBSHIntegrationTest.SERVICE;
    static long networkId;

    @BeforeAll
    static void beforeAll() {
        System.out.println("BTPBlockMessageTest:beforeAll start");
        BMVManagementTest.addVerifier(net, MockBMVIntegrationTest.mockBMV._address());
        networkId = MockGovIntegrationTest.openBTPNetwork("eth", link, bmc._address());
        BTPLinkManagementTest.addBTPLink(link, networkId);
        BMRManagementTest.addRelay(link, relay);

        BSHManagementTest.clearService(svc);
        BSHManagementTest.addService(svc, MockBSHIntegrationTest.mockBSH._address());
        System.out.println("BTPBlockMessageTest:beforeAll end");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("BTPBlockMessageTest:afterAll start");
        BSHManagementTest.clearService(svc);

        BMRManagementTest.clearRelay(link, relay);
        LinkManagementTest.clearLink(link);
        BMVManagementTest.clearVerifier(net);
        System.out.println("BTPBlockMessageTest:afterAll end");
    }

    @Test
    void sendMessageShouldSuccess() {
        //BSHMock.sendMessage -> ChainSCORE.sendBTPMessage
        BigInteger sn = BigInteger.ONE;
        byte[] payload = Faker.btpLink().toBytes();
        BigInteger nsn = bmc.getNetworkSn();

        BigInteger txSeq = BMCIntegrationTest.getStatus(bmc, link)
                .getTx_seq();
        Consumer<TransactionResult> checker = (txr) -> {
            assertEquals(txSeq.add(BigInteger.ONE),
                    BTPBlockIntegrationTest.nextMessageSn(txr, networkId));
        };
        checker = checker.andThen(BTPBlockIntegrationTest.btpMessageChecker(networkId, (msgList) -> {
            assertEquals(1, msgList.size());
            BTPMessage btpMessage = msgList.get(0);
            assertEquals(btpAddress.net(), btpMessage.getSrc());
            assertEquals(net, btpMessage.getDst());
            assertEquals(svc, btpMessage.getSvc());
            assertEquals(sn, btpMessage.getSn());
            assertEquals(nsn.add(BigInteger.ONE), btpMessage.getNsn());
            assertArrayEquals(payload, btpMessage.getPayload());
        }));
        checker = checker.andThen(MessageTest.btpEventChecker(
                btpAddress.net(),
                nsn.add(BigInteger.ONE),
                linkBtpAddress,
                BTPMessageCenter.Event.SEND));
        MockBSHIntegrationTest.mockBSH.sendMessage(
                checker,
                bmc._address(),
                net, svc, sn, payload);
    }

}
