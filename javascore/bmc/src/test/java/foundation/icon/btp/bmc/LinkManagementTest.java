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

package foundation.icon.btp.bmc;

import foundation.icon.btp.lib.BTPAddress;
import foundation.icon.btp.test.BTPIntegrationTest;
import foundation.icon.btp.test.MockBMVIntegrationTest;
import foundation.icon.jsonrpc.Address;
import foundation.icon.jsonrpc.model.TransactionResult;
import foundation.icon.score.test.ScoreIntegrationTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class LinkManagementTest implements BMCIntegrationTest {

    static BTPAddress linkBtpAddress = BTPIntegrationTest.Faker.btpLink();
    static String link = linkBtpAddress.toString();
    static BTPAddress secondLinkBtpAddress = BTPIntegrationTest.Faker.btpLink();
    static String secondLink = secondLinkBtpAddress.toString();

    static Consumer<List<BMCMessage>> initMessageChecker(List<String> links) {
        return (bmcMessages) -> {
            List<InitMessage> initMessages = BMCIntegrationTest.internalMessages(
                    bmcMessages, BTPMessageCenter.Internal.Init, InitMessage::fromBytes);
            assertEquals(1, initMessages.size());
            InitMessage initMessage = initMessages.get(0);
            assertEquals(links == null ? 0 : links.size(), initMessage.getLinks().length);
            assertTrue(links == null || links.containsAll(
                    Arrays.stream(initMessage.getLinks())
                            .map((BTPAddress::toString))
                            .collect(Collectors.toList())));
        };
    }

    static Consumer<List<BMCMessage>> linkMessageChecker(String link, int size) {
        return (bmcMessages) -> {
            List<LinkMessage> linkMessages = BMCIntegrationTest.internalMessages(
                    bmcMessages, BTPMessageCenter.Internal.Link, LinkMessage::fromBytes);
            assertEquals(size, linkMessages.size());
            assertTrue(linkMessages.stream()
                    .allMatch((linkMsg) -> linkMsg.getLink().toString().equals(link)));
        };
    }

    static Consumer<List<BMCMessage>> unlinkMessageChecker(String link, int size) {
        return (bmcMessages) -> {
            List<UnlinkMessage> unlinkMessages = BMCIntegrationTest.internalMessages(
                    bmcMessages, BTPMessageCenter.Internal.Unlink, UnlinkMessage::fromBytes);
            assertEquals(size, unlinkMessages.size());
            assertTrue(unlinkMessages.stream()
                    .allMatch((unlinkMsg) -> unlinkMsg.getLink().toString().equals(link)));
        };
    }

    static boolean isExistsLink(String link) {
        return Arrays.asList(bmc.getLinks()).contains(link);
    }

    static void addLink(String link) {
        List<String> links = Arrays.asList(bmc.getLinks());
        Consumer<TransactionResult> transactionResultChecker = (txr) ->
            initMessageChecker(links)
                    .accept(BMCIntegrationTest.bmcMessages(txr, (next) -> next.equals(link)));
        bmc.addLink(transactionResultChecker, link);
        assertTrue(isExistsLink(link));
    }

    static void removeLink(String link) {
        bmc.removeLink(link);
        assertFalse(isExistsLink(link));
    }

    static void clearLink(String link) {
        if (isExistsLink(link)) {
            System.out.println("clear link btpAddress:" + link);
            removeLink(link);
        }
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("LinkManagementTest:beforeAll start");
        Address mockBMVAddress = MockBMVIntegrationTest.mockBMV._address();
        BMVManagementTest.addVerifier(
                linkBtpAddress.net(), mockBMVAddress);
        BMVManagementTest.addVerifier(
                secondLinkBtpAddress.net(), mockBMVAddress);
        System.out.println("LinkManagementTest:beforeAll end");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("LinkManagementTest:afterAll start");
        BMVManagementTest.clearVerifier(linkBtpAddress.net());
        BMVManagementTest.clearVerifier(secondLinkBtpAddress.net());
        System.out.println("LinkManagementTest:afterAll end");
    }

    @Override
    public void clearIfExists(TestInfo testInfo) {
        clearLink(link);
        clearLink(secondLink);
    }

    @Test
    void addLinkShouldSuccess() {
        addLink(link);
    }

    @Test
    void addLinkShouldRevertAlreadyExists() {
        addLink(link);

        AssertBMCException.assertAlreadyExistsLink(() -> addLink(link));
    }

    @Test
    void addLinkShouldRevertAlreadyExistsIfRegisteredNetwork() {
        addLink(link);

        BTPAddress registeredNetworkLink = new BTPAddress(BTPAddress.PROTOCOL_BTP, linkBtpAddress.net(),
                ScoreIntegrationTest.Faker.address(Address.Type.CONTRACT).toString());
        AssertBMCException.assertAlreadyExistsLink(() -> addLink(registeredNetworkLink.toString()));
    }

    @Test
    void addLinkShouldRevertNotExistsBMV() {
        AssertBMCException.assertNotExistsBMV(
                () -> addLink(BTPIntegrationTest.Faker.btpLink().toString()));
    }

    @Test
    void removeLinkShouldSuccess() {
        addLink(link);

        removeLink(link);
    }

    @Test
    void removeLinkShouldRevertNotExists() {
        AssertBMCException.assertNotExistsLink(
                () -> removeLink(link));
    }

    @Test
    void removeLinkShouldClearRelays() {
        addLink(link);
        BMRManagementTest.addRelay(link, Address.of(defaultWallet));
        removeLink(link);

        //check relays of link is empty
        addLink(link);
        assertEquals(0, iconSpecific.getRelays(link).length);
    }

    @Test
    void addLinkShouldSendLinkMessageAndRemoveLinkShouldSendUnlinkMessage() {
        addLink(link);

        //addLinkShouldSendLinkMessage
        String secondLink = secondLinkBtpAddress.toString();
        List<String> links = Arrays.asList(bmc.getLinks());

        Consumer<TransactionResult> linkMessageCheck = (txr) -> {
            initMessageChecker(links)
                    .accept(BMCIntegrationTest.bmcMessages(txr, (next) -> next.equals(secondLink)));
            List<String> copy = new ArrayList<>(links);
            linkMessageChecker(secondLink, links.size())
                    .accept(BMCIntegrationTest.bmcMessages(txr, copy::remove));
            assertEquals(0, copy.size());
        };
        bmc.addLink(linkMessageCheck, secondLink);
        assertTrue(isExistsLink(secondLink));

        //RemoveLinkShouldSendUnlinkMessage
        Consumer<TransactionResult> unlinkMessageCheck = (txr) -> {
            List<String> copy = new ArrayList<>(links);
            unlinkMessageChecker(secondLink, links.size())
                    .accept(BMCIntegrationTest.bmcMessages(txr, copy::remove));
            assertEquals(0, copy.size());
        };
        bmc.removeLink(unlinkMessageCheck, secondLink);
        assertFalse(isExistsLink(secondLink));
    }

}
