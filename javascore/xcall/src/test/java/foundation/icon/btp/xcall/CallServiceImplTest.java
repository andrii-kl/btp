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

package foundation.icon.btp.xcall;

import foundation.icon.btp.lib.BTPAddress;
import foundation.icon.btp.test.BTPIntegrationTest;
import foundation.icon.btp.test.MockBMCIntegrationTest;
import foundation.icon.jsonrpc.Address;
import foundation.icon.score.test.AssertRevertedException;
import foundation.icon.score.test.ScoreIntegrationTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CallServiceImplTest implements CSIntegrationTest {
    static Address csAddress = callSvc._address();
    static Address sampleAddress = sampleClient._address();
    static Address fakeAddress = ScoreIntegrationTest.Faker.address(Address.Type.CONTRACT);
    static String linkNet = BTPIntegrationTest.Faker.btpNetwork();
    static BTPAddress to = new BTPAddress(linkNet, sampleAddress.toString());
    static BTPAddress fakeTo = new BTPAddress(linkNet, fakeAddress.toString());
    static BTPAddress bmcBtpAddress;
    static BigInteger srcSn = BigInteger.ZERO;
    static BigInteger reqId = BigInteger.ZERO;
    static Map<BigInteger, MessageRequest> requestMap = new HashMap<>();

    static BigInteger forwardFee, backwardFee, protocolFee;
    static Address protocolFeeHandler = ScoreIntegrationTest.Faker.address(Address.Type.EOA);
    static Map<String, BigInteger> accruedFees = new HashMap<>();

    private static class MessageRequest {
        private final byte[] data;
        private final byte[] rollback;

        public MessageRequest(byte[] data, byte[] rollback) {
            this.data = data;
            this.rollback = rollback;
        }

        public byte[] getData() {
            return data;
        }

        public byte[] getRollback() {
            return rollback;
        }
    }

    @BeforeAll
    static void beforeAll() {
        forwardFee = BigInteger.TEN;
        backwardFee = BigInteger.TWO;
        MockBMCIntegrationTest.mockBMC.setFee(forwardFee, backwardFee);

        protocolFee = BigInteger.valueOf(3);
        feeManager.setProtocolFee(protocolFee);
        assertNull(feeManager.getProtocolFeeHandler());

        bmcBtpAddress = BTPAddress.valueOf(MockBMCIntegrationTest.mockBMC.getBtpAddress());
    }

    private BigInteger getNextSn() {
        return getNextSn(BigInteger.ONE);
    }

    private BigInteger getNextSn(BigInteger inc) {
        srcSn = srcSn.add(inc);
        return srcSn;
    }

    private BigInteger getNextReqId() {
        reqId = reqId.add(BigInteger.ONE);
        return reqId;
    }

    private BigInteger getFee(String net, boolean rollback) {
        return feeManager.getFee(net, rollback);
    }

    private void accumulateFee(BigInteger totalFee, BigInteger protocolFee) {
        BigInteger relayFee = totalFee.subtract(protocolFee);
        accruedFees.merge("relay", relayFee, (a, b) -> b.add(a));
        accruedFees.merge("protocol", protocolFee, (a, b) -> b.add(a));
    }

    @Order(0)
    @Test
    void sendCallMessageWithoutRollback() {
        byte[] data = "sendCallMessageWithoutRollback".getBytes();
        var sn = getNextSn();
        requestMap.put(sn, new MessageRequest(data, null));
        var request = new CSMessageRequest(sampleAddress.toString(), to.account(), sn, false, data);
        var checker = MockBMCIntegrationTest.sendMessageEvent((el) -> {
            assertEquals(linkNet, el.getTo());
            assertEquals(CallService.NAME, el.getSvc());
            assertEquals(BigInteger.ZERO, el.getSn()); // one-way message
            CSMessage csMessage = CSMessage.fromBytes(el.getMsg());
            assertEquals(CSMessage.REQUEST, csMessage.getType());
            AssertCallService.assertEqualsCSMessageRequest(request, CSMessageRequest.fromBytes(csMessage.getData()));
        });
        BigInteger fee = getFee(to.net(), false);
        assertEquals(forwardFee.add(protocolFee), fee);
        accumulateFee(fee, protocolFee);
        Map<String, Object> params = new HashMap<>();
        params.put("_to", to.toString());
        params.put("_data", data);
        checker.accept(sampleClient._send(fee, "sendMessage", params));
    }

    @Order(1)
    @Test
    void handleBTPMessageShouldEmitCallMessage() {
        var from = new BTPAddress(linkNet, sampleAddress.toString());
        var reqId = getNextReqId();
        byte[] data = requestMap.get(srcSn).getData();
        var request = new CSMessageRequest(from.account(), to.account(), srcSn, false, data);
        var csMsg = new CSMessage(CSMessage.REQUEST, request.toBytes());
        var checker = CSIntegrationTest.callMessageEvent((el) -> {
            assertEquals(from.toString(), el.getFrom());
            assertEquals(sampleAddress.toString(), el.getTo());
            assertEquals(srcSn, el.getSn());
            assertEquals(reqId, el.getReqId());
            assertArrayEquals(request.getData(), el.getData());
        });
        MockBMCIntegrationTest.mockBMC.handleBTPMessage(
                checker, csAddress,
                linkNet, CallService.NAME, srcSn, csMsg.toBytes());
    }

    @Order(2)
    @Test
    void executeCallWithoutSuccessResponse() {
        var from = new BTPAddress(linkNet, sampleAddress.toString());
        var checker = CSIntegrationTest.messageReceivedEvent((el) -> {
            assertEquals(from.toString(), el.getFrom());
            assertArrayEquals(requestMap.get(srcSn).getData(), el.getData());
        }).andThen(MockBMCIntegrationTest.sendMessageEventShouldNotExists());
        callSvc.executeCall(checker, reqId);
    }

    @Order(3)
    @Test
    void handleBTPMessageWithSuccessResponseButNoRequestEntry() {
        var dstSn = BigInteger.ONE;
        var response = new CSMessageResponse(srcSn, CSMessageResponse.SUCCESS, null);
        var csMsg = new CSMessage(CSMessage.RESPONSE, response.toBytes());
        var checker = CSIntegrationTest.callRequestClearedEventShouldNotExists();
        MockBMCIntegrationTest.mockBMC.handleBTPMessage(
                checker, csAddress,
                linkNet, CallService.NAME, dstSn, csMsg.toBytes());
    }

    @Order(4)
    @Test
    void handleBTPMessageWithFailureResponseButNoRequestEntry() {
        var dstSn = BigInteger.TWO;
        var response = new CSMessageResponse(srcSn, CSMessageResponse.FAILURE, "java.lang.IllegalArgumentException");
        var csMsg = new CSMessage(CSMessage.RESPONSE, response.toBytes());
        var checker = CSIntegrationTest.rollbackMessageEventShouldNotExists();
        MockBMCIntegrationTest.mockBMC.handleBTPMessage(
                checker, csAddress,
                linkNet, CallService.NAME, dstSn, csMsg.toBytes());
    }

    @Order(5)
    @Test
    @SuppressWarnings("ThrowableNotThrown")
    void maxPayloadsTest() {
        byte[][][] cases = {
                {new byte[CallServiceImpl.MAX_DATA_SIZE + 1], null},
                {new byte[CallServiceImpl.MAX_DATA_SIZE], new byte[CallServiceImpl.MAX_ROLLBACK_SIZE + 1]},
        };
        for (var c : cases) {
            Map<String, Object> params = new HashMap<>();
            params.put("_to", to.toString());
            params.put("_data", c[0]);
            if (c[1] != null) {
                params.put("_rollback", c[1]);
            }
            BigInteger fee = getFee(to.net(), c[1] != null);
            AssertRevertedException.assertUserReverted(0, () ->
                    sampleClient._send(fee, "sendMessage", params)
            );
        }
    }

    @Order(10)
    @Test
    void sendCallMessageWithRollback() {
        _sendCallMessageWithRollback(BigInteger.ONE);
    }

    private void _sendCallMessageWithRollback(BigInteger inc) {
        byte[] data = "sendCallMessageWithRollback".getBytes();
        byte[] rollback = "ThisIsRollbackMessage".getBytes();
        var sn = getNextSn(inc);
        requestMap.put(sn, new MessageRequest(data, rollback));
        var request = new CSMessageRequest(sampleAddress.toString(), fakeTo.account(), sn, true, data);
        var checker = MockBMCIntegrationTest.sendMessageEvent((el) -> {
            assertEquals(linkNet, el.getTo());
            assertEquals(CallService.NAME, el.getSvc());
            assertEquals(sn, el.getSn());
            CSMessage csMessage = CSMessage.fromBytes(el.getMsg());
            assertEquals(CSMessage.REQUEST, csMessage.getType());
            AssertCallService.assertEqualsCSMessageRequest(request, CSMessageRequest.fromBytes(csMessage.getData()));
        });
        BigInteger fee = getFee(to.net(), true);
        accumulateFee(fee, protocolFee);
        Map<String, Object> params = new HashMap<>();
        params.put("_to", fakeTo.toString());
        params.put("_data", data);
        params.put("_rollback", rollback);
        checker.accept(sampleClient._send(fee, "sendMessage", params));
    }

    @Order(11)
    @Test
    void executeCallWithFailureResponse() {
        // relay the message first
        var from = new BTPAddress(linkNet, sampleAddress.toString());
        byte[] data = requestMap.get(srcSn).getData();
        var request = new CSMessageRequest(from.account(), fakeTo.account(), srcSn, true, data);
        var csMsg = new CSMessage(CSMessage.REQUEST, request.toBytes());
        MockBMCIntegrationTest.mockBMC.handleBTPMessage(csAddress,
                linkNet, CallService.NAME, srcSn, csMsg.toBytes());
        // add response info to BMC mock
        MockBMCIntegrationTest.mockBMC.addResponse(fakeTo.net(), CallService.NAME, srcSn);

        // expect executeCall failure
        var reqId = getNextReqId();
        var response = new CSMessageResponse(srcSn, CSMessageResponse.FAILURE, "java.lang.IllegalArgumentException");
        var checker = MockBMCIntegrationTest.sendMessageEvent((el) -> {
            assertEquals(linkNet, el.getTo());
            assertEquals(CallService.NAME, el.getSvc());
            CSMessage csMessage = CSMessage.fromBytes(el.getMsg());
            assertEquals(CSMessage.RESPONSE, csMessage.getType());
            AssertCallService.assertEqualsCSMessageResponse(response, CSMessageResponse.fromBytes(csMessage.getData()));
        });
        callSvc.executeCall(checker, reqId);
    }

    @Order(12)
    @Test
    @SuppressWarnings("ThrowableNotThrown")
    void executeRollbackEarlyCallShouldFail() {
        AssertRevertedException.assertUserReverted(0, () ->
                callSvc.executeRollback(srcSn)
        );
    }

    @Order(12)
    @Test
    @SuppressWarnings("ThrowableNotThrown")
    void handleCallMessageFromInvalidCaller() {
        var from = new BTPAddress(bmcBtpAddress.net(), csAddress.toString());
        byte[] fakeRollback = "ThisIsFakeRollback".getBytes();
        AssertRevertedException.assertUserReverted(0, () ->
                sampleClient.handleCallMessage(from.toString(), fakeRollback)
        );
    }

    @Order(13)
    @Test
    void handleBTPMessageWithFailureResponse() {
        var dstSn = BigInteger.TWO;
        var response = new CSMessageResponse(srcSn, CSMessageResponse.FAILURE, "java.lang.IllegalArgumentException");
        var csMsg = new CSMessage(CSMessage.RESPONSE, response.toBytes());
        var checker = CSIntegrationTest.rollbackMessageEvent((el) -> {
            assertEquals(srcSn, el.getSn());
        }).andThen(CSIntegrationTest.callRequestClearedEventShouldNotExists());
        MockBMCIntegrationTest.mockBMC.handleBTPMessage(
                checker, csAddress,
                linkNet, CallService.NAME, dstSn, csMsg.toBytes());
    }

    @Order(14)
    @Test
    void executeRollbackWithFailureResponse() {
        var from = new BTPAddress(bmcBtpAddress.net(), csAddress.toString());
        var checker = CSIntegrationTest.rollbackDataReceivedEvent((el) -> {
            assertEquals(from.toString(), el.getFrom());
            assertEquals(srcSn, el.getSsn());
            assertArrayEquals(requestMap.get(srcSn).getRollback(), el.getRollback());
        }).andThen(CSIntegrationTest.callRequestClearedEvent((el) -> {
            assertEquals(srcSn, el.getSn());
        }));
        callSvc.executeRollback(checker, srcSn);
    }

    @Order(15)
    @Test
    void setProtocolFeeHandler() {
        BigInteger feeBalance = callSvc._balance();
        assertEquals(accruedFees.get("protocol"), feeBalance);

        assertEquals(BigInteger.ZERO, callSvc._balance(protocolFeeHandler));
        feeManager.setProtocolFeeHandler(protocolFeeHandler);
        assertEquals(feeBalance, callSvc._balance(protocolFeeHandler));
    }

    @Order(16)
    @Test
    void handleBTPMessageWithSuccessResponse() {
        // prepare another call request
        _sendCallMessageWithRollback(BigInteger.ONE);

        // check the CallRequestCleared event
        var dstSn = BigInteger.TEN;
        var response = new CSMessageResponse(srcSn, CSMessageResponse.SUCCESS, null);
        var csMsg = new CSMessage(CSMessage.RESPONSE, response.toBytes());
        var checker = CSIntegrationTest.callRequestClearedEvent((el) -> {
            assertEquals(srcSn, el.getSn());
        }).andThen(CSIntegrationTest.rollbackMessageEventShouldNotExists());
        MockBMCIntegrationTest.mockBMC.handleBTPMessage(
                checker, csAddress,
                linkNet, CallService.NAME, dstSn, csMsg.toBytes());
    }

    @Order(17)
    @Test
    void verifyFeeTransfer() {
        assertEquals(BigInteger.ZERO, callSvc._balance());
        assertEquals(accruedFees.get("protocol"), callSvc._balance(protocolFeeHandler));
    }

    @Order(18)
    @Test
    void resetProtocolFeeHandler() {
        assertEquals(protocolFeeHandler, feeManager.getProtocolFeeHandler());
        feeManager._send("setProtocolFeeHandler", null);
        assertNull(feeManager.getProtocolFeeHandler());
    }

    @Order(20)
    @Test
    void handleBTPErrorTest() {
        // prepare another call request
        _sendCallMessageWithRollback(BigInteger.ONE);

        // check the BTP error message
        var checker = CSIntegrationTest.rollbackMessageEvent((el) -> {
            assertEquals(srcSn, el.getSn());
        }).andThen(CSIntegrationTest.callRequestClearedEventShouldNotExists());
        MockBMCIntegrationTest.mockBMC.handleBTPError(
                checker, csAddress,
                bmcBtpAddress.toString(), CallService.NAME, srcSn, 1, "BTPError");
    }

    @Order(21)
    @Test
    void executeRollbackWithBTPError() {
        var from = new BTPAddress(bmcBtpAddress.net(), csAddress.toString());
        var checker = CSIntegrationTest.rollbackDataReceivedEvent((el) -> {
            assertEquals(from.toString(), el.getFrom());
            assertArrayEquals(requestMap.get(srcSn).getRollback(), el.getRollback());
        }).andThen(CSIntegrationTest.callRequestClearedEvent((el) -> {
            assertEquals(srcSn, el.getSn());
        }));
        callSvc.executeRollback(checker, srcSn);
    }
}
