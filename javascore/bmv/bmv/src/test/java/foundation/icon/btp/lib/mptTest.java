package foundation.icon.btp.lib.mpt;

import java.util.Arrays;

import foundation.icon.btp.lib.mpt.Nibbles;
import foundation.icon.btp.lib.utils.HexConverter;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestMethodOrder;
import score.Context;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import scorex.util.ArrayList;

import org.junit.jupiter.api.Disabled;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;

// @Disabled
@TestMethodOrder(OrderAnnotation.class)
class MPTest {
    @Test
    @Order(1)
    public void proveEventStorage() {
        ArrayList<MPTNode> proofs = new ArrayList<MPTNode>(5);
        proofs.add(new MPTNode(HexConverter.hexStringToByteArray("80499c808195b6400caeaaceee2debc277ceadf67ad2af2ddd02b815476ad91d303076c580fc6822539a4dd76f5bd08ebce76152f9043fe167826db92c7569e43c16d675af80f26e60648c101cd4e706c69caa588a40c5215c052e849fc410681277e12c0e5b80a108353045683e792e050af2eda268c860fbcd757d59dd5c194c6c726308459d80102c09e404e12ce2a6e71e214ea009053358e7f3de223457de265a6faab6e454803f6f047a1cb4d78714bcb64168513cca61d3120e8c0f5c30affb7393620f84b480623848e735dd707e95d569e06cbca4c4437a925d0887bfd1ce67b035d44aec80")));
        proofs.add(new MPTNode(HexConverter.hexStringToByteArray("9eaa394eea5630e07c48ae0c9558cef7299f80da681d5efb680bef057a58ffbaa7551590425911dc0b272c9da47fcde38f3fbe807da0cb76c07e5a74b3303c95a56cf06da88afdf25c39db028498d8e8cd2287694c5f0684a022a34dd8bfa2baaf44f172b7100401802f965a5e16a4a150ab5c9bf54c8a46eb01b020f026b3f608f2f3b65a219be1e3804ce3f5738277bd4abdf453f58df9c2da09cc53dee7bff7471ed6d2ed6bd26ee180f0ce00fa24fd38f8a6f4f65268bf304215733f9ca85d708581915983a1240342807bf5646da3793f6a01641f0b3808f1eb1959dcac33a5cfcea14ae652e8cfba864c5f021aab032aaa6e946ca50ad39ab666030401705f09cce9c888469bb1a0dceaa129672ef8287420706f6c6b61646f74")));
        proofs.add(new MPTNode(HexConverter.hexStringToByteArray("80ffff801ee4115c4d5894ff9b5e6bb83611e2a8a8503482fe35a3f086d500fd1f111961806fe86874420b0da66e64aceb6faf0923bcfcac83ab77a12fcaf53915fe005dbe807ea888eb72a4703a6a2049ee5f8a4b57efb9ff127c33f4fc0a1b00f36f2ec6d080415256d8937fed4618e92d39ed6a96f986e4303ca3689b33c59ade227af7909a806af5c4f477504b627b1af9c106960aefe0e0a0632386b542897157486e0145a480142d00923c7e321f47d9eb0918514f983f9916c6db8c952d36fa5366da20ce9a800eb754c27d6302344f80fc4f785eae09c7c6acf58ee0ebddbd2f1755eb37a7de80487b2dc7bef5a919013bf34dfca66bbaae571ebedbdb0eeb0c8149dfa0d3545c805ef8441d86999ada141d6e18656a9243ceb6fadf27dc3c600d65d64488232c04800d7b7c347d9d129a1c9596f6ce1b8b1adf0d408acf677571a198c3eda6ad9b6280b54561a0ca6f56c484ed434f3cdd323985e74088f456e6c32ed47bd65dd7269f8074b8fd9e7670794f9cba3bdf1ac05b9104da6d27a42113f2f3d95a9ee90dc0298093096e5243ed57428d45ef91c5fcf8ff06179628488202e5d4864867b9674be280b911d65bff5f4ce6c0520d83d55b8ee182c7e87664f67664849276b731b4511780aa70f608bff62a7658840159c5f90224dd41de9b5a2232c2d2910e6179fb707f80a50e07dd134bf95fc2ff5676b0ec5e34d4803f0a679615405bf25cc87394fd5f")));
        proofs.add(new MPTNode(HexConverter.hexStringToByteArray("8081048066460feb98ea680339fdf0fdca521f4f090296634456658856df27f3256d4fd1545e8d434d6125b40443fe11fd292d13a4100300000080e0d23e3681be10a9c7ca00a301e523d9519222737468ee0af39215a858266385")));
        proofs.add(new MPTNode(HexConverter.hexStringToByteArray("5ed41e5e16056765bc8461851072c9d74c04000000000000002878180b00000000020000")));

        byte[] key = HexConverter.hexStringToByteArray("26aa394eea5630e07c48ae0c9558cef780d41e5e16056765bc8461851072c9d7");
        byte[] root = HexConverter.hexStringToByteArray("c40efd7458262583b22ddcb17b434a40b802116ed24bf6cdd0dbce4face6126e");

        Nibbles keyNibbles = new Nibbles(key, false);
        byte[] provingValue = MerklePatriciaTree.prove(root, keyNibbles, proofs);

        assertArrayEquals(provingValue, HexConverter.hexStringToByteArray("04000000000000002878180b00000000020000"));
    }

    @Test
    @Order(2)
    public void prove() {
        ArrayList<MPTNode> proofs = new ArrayList<MPTNode>(5);
        proofs.add(new MPTNode(HexConverter.hexStringToByteArray("80ffff801ee4115c4d5894ff9b5e6bb83611e2a8a8503482fe35a3f086d500fd1f111961806fe86874420b0da66e64aceb6faf0923bcfcac83ab77a12fcaf53915fe005dbe807ea888eb72a4703a6a2049ee5f8a4b57efb9ff127c33f4fc0a1b00f36f2ec6d080415256d8937fed4618e92d39ed6a96f986e4303ca3689b33c59ade227af7909a806af5c4f477504b627b1af9c106960aefe0e0a0632386b542897157486e0145a480142d00923c7e321f47d9eb0918514f983f9916c6db8c952d36fa5366da20ce9a800eb754c27d6302344f80fc4f785eae09c7c6acf58ee0ebddbd2f1755eb37a7de80487b2dc7bef5a919013bf34dfca66bbaae571ebedbdb0eeb0c8149dfa0d3545c805ef8441d86999ada141d6e18656a9243ceb6fadf27dc3c600d65d64488232c04800d7b7c347d9d129a1c9596f6ce1b8b1adf0d408acf677571a198c3eda6ad9b6280b54561a0ca6f56c484ed434f3cdd323985e74088f456e6c32ed47bd65dd7269f8074b8fd9e7670794f9cba3bdf1ac05b9104da6d27a42113f2f3d95a9ee90dc0298093096e5243ed57428d45ef91c5fcf8ff06179628488202e5d4864867b9674be280b911d65bff5f4ce6c0520d83d55b8ee182c7e87664f67664849276b731b4511780aa70f608bff62a7658840159c5f90224dd41de9b5a2232c2d2910e6179fb707f80a50e07dd134bf95fc2ff5676b0ec5e34d4803f0a679615405bf25cc87394fd5f")));
        proofs.add(new MPTNode(HexConverter.hexStringToByteArray("80499c808195b6400caeaaceee2debc277ceadf67ad2af2ddd02b815476ad91d303076c580fc6822539a4dd76f5bd08ebce76152f9043fe167826db92c7569e43c16d675af80f26e60648c101cd4e706c69caa588a40c5215c052e849fc410681277e12c0e5b80a108353045683e792e050af2eda268c860fbcd757d59dd5c194c6c726308459d80102c09e404e12ce2a6e71e214ea009053358e7f3de223457de265a6faab6e454803f6f047a1cb4d78714bcb64168513cca61d3120e8c0f5c30affb7393620f84b480623848e735dd707e95d569e06cbca4c4437a925d0887bfd1ce67b035d44aec80")));
        proofs.add(new MPTNode(HexConverter.hexStringToByteArray("800404545ea5c1b19ab7a04f536c519aca4983ac1011094400545e98fdbe9ce6c55837576c60c7af38501001000000")));
        proofs.add(new MPTNode(HexConverter.hexStringToByteArray("9eaa394eea5630e07c48ae0c9558cef7299f80da681d5efb680bef057a58ffbaa7551590425911dc0b272c9da47fcde38f3fbe807da0cb76c07e5a74b3303c95a56cf06da88afdf25c39db028498d8e8cd2287694c5f0684a022a34dd8bfa2baaf44f172b7100401802f965a5e16a4a150ab5c9bf54c8a46eb01b020f026b3f608f2f3b65a219be1e3804ce3f5738277bd4abdf453f58df9c2da09cc53dee7bff7471ed6d2ed6bd26ee180f0ce00fa24fd38f8a6f4f65268bf304215733f9ca85d708581915983a1240342807bf5646da3793f6a01641f0b3808f1eb1959dcac33a5cfcea14ae652e8cfba864c5f021aab032aaa6e946ca50ad39ab666030401705f09cce9c888469bb1a0dceaa129672ef8287420706f6c6b61646f74")));
        
        byte[] key = HexConverter.hexStringToByteArray("26aa394eea5630e07c48ae0c9558cef702a5c1b19ab7a04f536c519aca4983ac");
        byte[] root = HexConverter.hexStringToByteArray("c40efd7458262583b22ddcb17b434a40b802116ed24bf6cdd0dbce4face6126e");

        Nibbles keyNibbles = new Nibbles(key, false);
        byte[] provingValue = MerklePatriciaTree.prove(root, keyNibbles, proofs);

        assertArrayEquals(provingValue, HexConverter.hexStringToByteArray("11094400"));
    }

    @Test
    @Order(3)
    public void proveFailMissingProof() {
        ArrayList<MPTNode> proofs = new ArrayList<MPTNode>(5);
        proofs.add(new MPTNode(HexConverter.hexStringToByteArray("80499c808195b6400caeaaceee2debc277ceadf67ad2af2ddd02b815476ad91d303076c580fc6822539a4dd76f5bd08ebce76152f9043fe167826db92c7569e43c16d675af80f26e60648c101cd4e706c69caa588a40c5215c052e849fc410681277e12c0e5b80a108353045683e792e050af2eda268c860fbcd757d59dd5c194c6c726308459d80102c09e404e12ce2a6e71e214ea009053358e7f3de223457de265a6faab6e454803f6f047a1cb4d78714bcb64168513cca61d3120e8c0f5c30affb7393620f84b480623848e735dd707e95d569e06cbca4c4437a925d0887bfd1ce67b035d44aec80")));
        proofs.add(new MPTNode(HexConverter.hexStringToByteArray("80ffff801ee4115c4d5894ff9b5e6bb83611e2a8a8503482fe35a3f086d500fd1f111961806fe86874420b0da66e64aceb6faf0923bcfcac83ab77a12fcaf53915fe005dbe807ea888eb72a4703a6a2049ee5f8a4b57efb9ff127c33f4fc0a1b00f36f2ec6d080415256d8937fed4618e92d39ed6a96f986e4303ca3689b33c59ade227af7909a806af5c4f477504b627b1af9c106960aefe0e0a0632386b542897157486e0145a480142d00923c7e321f47d9eb0918514f983f9916c6db8c952d36fa5366da20ce9a800eb754c27d6302344f80fc4f785eae09c7c6acf58ee0ebddbd2f1755eb37a7de80487b2dc7bef5a919013bf34dfca66bbaae571ebedbdb0eeb0c8149dfa0d3545c805ef8441d86999ada141d6e18656a9243ceb6fadf27dc3c600d65d64488232c04800d7b7c347d9d129a1c9596f6ce1b8b1adf0d408acf677571a198c3eda6ad9b6280b54561a0ca6f56c484ed434f3cdd323985e74088f456e6c32ed47bd65dd7269f8074b8fd9e7670794f9cba3bdf1ac05b9104da6d27a42113f2f3d95a9ee90dc0298093096e5243ed57428d45ef91c5fcf8ff06179628488202e5d4864867b9674be280b911d65bff5f4ce6c0520d83d55b8ee182c7e87664f67664849276b731b4511780aa70f608bff62a7658840159c5f90224dd41de9b5a2232c2d2910e6179fb707f80a50e07dd134bf95fc2ff5676b0ec5e34d4803f0a679615405bf25cc87394fd5f")));
        proofs.add(new MPTNode(HexConverter.hexStringToByteArray("9eaa394eea5630e07c48ae0c9558cef7299f80da681d5efb680bef057a58ffbaa7551590425911dc0b272c9da47fcde38f3fbe807da0cb76c07e5a74b3303c95a56cf06da88afdf25c39db028498d8e8cd2287694c5f0684a022a34dd8bfa2baaf44f172b7100401802f965a5e16a4a150ab5c9bf54c8a46eb01b020f026b3f608f2f3b65a219be1e3804ce3f5738277bd4abdf453f58df9c2da09cc53dee7bff7471ed6d2ed6bd26ee180f0ce00fa24fd38f8a6f4f65268bf304215733f9ca85d708581915983a1240342807bf5646da3793f6a01641f0b3808f1eb1959dcac33a5cfcea14ae652e8cfba864c5f021aab032aaa6e946ca50ad39ab666030401705f09cce9c888469bb1a0dceaa129672ef8287420706f6c6b61646f74")));
        // proofs.add(new MPTNode(HexConverter.hexStringToByteArray("8081048066460feb98ea680339fdf0fdca521f4f090296634456658856df27f3256d4fd1545e8d434d6125b40443fe11fd292d13a4100300000080e0d23e3681be10a9c7ca00a301e523d9519222737468ee0af39215a858266385")));

        byte[] key = HexConverter.hexStringToByteArray("26aa394eea5630e07c48ae0c9558cef780d41e5e16056765bc8461851072c9d7");
        byte[] root = HexConverter.hexStringToByteArray("c40efd7458262583b22ddcb17b434a40b802116ed24bf6cdd0dbce4face6126e");

        Nibbles keyNibbles = new Nibbles(key, false);

        AssertionError thrown = assertThrows(
            AssertionError.class,
           () -> MerklePatriciaTree.prove(root, keyNibbles, proofs)
        );

        assertTrue(thrown.getMessage().contains("MPT missing proof"));
    }

    @Test
    @Order(4)
    public void proveFailMismatchNibbleOnBranch() {
        ArrayList<MPTNode> proofs = new ArrayList<MPTNode>(5);
        proofs.add(new MPTNode(HexConverter.hexStringToByteArray("800404545ea5c1b19ab7a04f536c519aca4983ac1011094400545e98fdbe9ce6c55837576c60c7af38501001000000")));
        proofs.add(new MPTNode(HexConverter.hexStringToByteArray("80ffff801ee4115c4d5894ff9b5e6bb83611e2a8a8503482fe35a3f086d500fd1f111961806fe86874420b0da66e64aceb6faf0923bcfcac83ab77a12fcaf53915fe005dbe807ea888eb72a4703a6a2049ee5f8a4b57efb9ff127c33f4fc0a1b00f36f2ec6d080415256d8937fed4618e92d39ed6a96f986e4303ca3689b33c59ade227af7909a806af5c4f477504b627b1af9c106960aefe0e0a0632386b542897157486e0145a480142d00923c7e321f47d9eb0918514f983f9916c6db8c952d36fa5366da20ce9a800eb754c27d6302344f80fc4f785eae09c7c6acf58ee0ebddbd2f1755eb37a7de80487b2dc7bef5a919013bf34dfca66bbaae571ebedbdb0eeb0c8149dfa0d3545c805ef8441d86999ada141d6e18656a9243ceb6fadf27dc3c600d65d64488232c04800d7b7c347d9d129a1c9596f6ce1b8b1adf0d408acf677571a198c3eda6ad9b6280b54561a0ca6f56c484ed434f3cdd323985e74088f456e6c32ed47bd65dd7269f8074b8fd9e7670794f9cba3bdf1ac05b9104da6d27a42113f2f3d95a9ee90dc0298093096e5243ed57428d45ef91c5fcf8ff06179628488202e5d4864867b9674be280b911d65bff5f4ce6c0520d83d55b8ee182c7e87664f67664849276b731b4511780aa70f608bff62a7658840159c5f90224dd41de9b5a2232c2d2910e6179fb707f80a50e07dd134bf95fc2ff5676b0ec5e34d4803f0a679615405bf25cc87394fd5f")));
        proofs.add(new MPTNode(HexConverter.hexStringToByteArray("80499c808195b6400caeaaceee2debc277ceadf67ad2af2ddd02b815476ad91d303076c580fc6822539a4dd76f5bd08ebce76152f9043fe167826db92c7569e43c16d675af807f533d66adf5ba7dbaf627fefd249d3b24328142f5f56cbf5ca8df2ccaf595d280a108353045683e792e050af2eda268c860fbcd757d59dd5c194c6c726308459d80102c09e404e12ce2a6e71e214ea009053358e7f3de223457de265a6faab6e454803f6f047a1cb4d78714bcb64168513cca61d3120e8c0f5c30affb7393620f84b480623848e735dd707e95d569e06cbca4c4437a925d0887bfd1ce67b035d44aec80")));
        proofs.add(new MPTNode(HexConverter.hexStringToByteArray("9eaa394eea5630e07c48ae0c9558cef7299f80da681d5efb680bef057a58ffbaa7551590425911dc0b272c9da47fcde38f3fbe807da0cb76c07e5a74b3303c95a56cf06da88afdf25c39db028498d8e8cd2287694c5f0684a022a34dd8bfa2baaf44f172b7100401802f965a5e16a4a150ab5c9bf54c8a46eb01b020f026b3f608f2f3b65a219be1e3804ce3f5738277bd4abdf453f58df9c2da09cc53dee7bff7471ed6d2ed6bd26ee180f0ce00fa24fd38f8a6f4f65268bf304215733f9ca85d708581915983a1240342807bf5646da3793f6a01641f0b3808f1eb1959dcac33a5cfcea14ae652e8cfba864c5f021aab032aaa6e946ca50ad39ab666030401705f09cce9c888469bb1a0dceaa129672ef8287420706f6c6b61646f74")));

        byte[] key = HexConverter.hexStringToByteArray("26aa394eea5630e07c48ae0c9558cef702a5c1b19ab7a04f536c519aca4983ac");
        byte[] root = HexConverter.hexStringToByteArray("13235466c4b6d0bccb6cfae3767a8cce52c485eb152208d1e24730cdce4058cd");

        Nibbles keyNibbles = new Nibbles(key, false);

        AssertionError thrown = assertThrows(
            AssertionError.class,
           () -> MerklePatriciaTree.prove(root, keyNibbles, proofs)
        );

        assertTrue(thrown.getMessage().contains("invalid MPT proof"));
    }

    @Test
    @Order(5)
    public void proveCanNotFindDataInBranchNode() {
        ArrayList<MPTNode> proofs = new ArrayList<MPTNode>(5);
        proofs.add(new MPTNode(HexConverter.hexStringToByteArray("80ffff801ee4115c4d5894ff9b5e6bb83611e2a8a8503482fe35a3f086d500fd1f111961806fe86874420b0da66e64aceb6faf0923bcfcac83ab77a12fcaf53915fe005dbe807ea888eb72a4703a6a2049ee5f8a4b57efb9ff127c33f4fc0a1b00f36f2ec6d080415256d8937fed4618e92d39ed6a96f986e4303ca3689b33c59ade227af7909a806af5c4f477504b627b1af9c106960aefe0e0a0632386b542897157486e0145a480142d00923c7e321f47d9eb0918514f983f9916c6db8c952d36fa5366da20ce9a800eb754c27d6302344f80fc4f785eae09c7c6acf58ee0ebddbd2f1755eb37a7de80487b2dc7bef5a919013bf34dfca66bbaae571ebedbdb0eeb0c8149dfa0d3545c805ef8441d86999ada141d6e18656a9243ceb6fadf27dc3c600d65d64488232c04800d7b7c347d9d129a1c9596f6ce1b8b1adf0d408acf677571a198c3eda6ad9b6280b54561a0ca6f56c484ed434f3cdd323985e74088f456e6c32ed47bd65dd7269f8074b8fd9e7670794f9cba3bdf1ac05b9104da6d27a42113f2f3d95a9ee90dc0298093096e5243ed57428d45ef91c5fcf8ff06179628488202e5d4864867b9674be280b911d65bff5f4ce6c0520d83d55b8ee182c7e87664f67664849276b731b4511780aa70f608bff62a7658840159c5f90224dd41de9b5a2232c2d2910e6179fb707f80a50e07dd134bf95fc2ff5676b0ec5e34d4803f0a679615405bf25cc87394fd5f")));
        proofs.add(new MPTNode(HexConverter.hexStringToByteArray("800404545ea5c1b19ab7a04f536c519aca4983ac1011094400545e98fdbe9ce6c55837576c60c7af38501001000000")));
        proofs.add(new MPTNode(HexConverter.hexStringToByteArray("80499c808195b6400caeaaceee2debc277ceadf67ad2af2ddd02b815476ad91d303076c580fc6822539a4dd76f5bd08ebce76152f9043fe167826db92c7569e43c16d675af807f533d66adf5ba7dbaf627fefd249d3b24328142f5f56cbf5ca8df2ccaf595d280a108353045683e792e050af2eda268c860fbcd757d59dd5c194c6c726308459d80102c09e404e12ce2a6e71e214ea009053358e7f3de223457de265a6faab6e454803f6f047a1cb4d78714bcb64168513cca61d3120e8c0f5c30affb7393620f84b480623848e735dd707e95d569e06cbca4c4437a925d0887bfd1ce67b035d44aec80")));
        proofs.add(new MPTNode(HexConverter.hexStringToByteArray("9eaa394eea5630e07c48ae0c9558cef7299f80da681d5efb680bef057a58ffbaa7551590425911dc0b272c9da47fcde38f3fbe807da0cb76c07e5a74b3303c95a56cf06da88afdf25c39db028498d8e8cd2287694c5f0684a022a34dd8bfa2baaf44f172b7100401802f965a5e16a4a150ab5c9bf54c8a46eb01b020f026b3f608f2f3b65a219be1e3804ce3f5738277bd4abdf453f58df9c2da09cc53dee7bff7471ed6d2ed6bd26ee180f0ce00fa24fd38f8a6f4f65268bf304215733f9ca85d708581915983a1240342807bf5646da3793f6a01641f0b3808f1eb1959dcac33a5cfcea14ae652e8cfba864c5f021aab032aaa6e946ca50ad39ab666030401705f09cce9c888469bb1a0dceaa129672ef8287420706f6c6b61646f74")));

        byte[] key = HexConverter.hexStringToByteArray("26aa394eea5630e07c48ae0c9558cef7");
        byte[] root = HexConverter.hexStringToByteArray("c40efd7458262583b22ddcb17b434a40b802116ed24bf6cdd0dbce4face6126e");

        Nibbles keyNibbles = new Nibbles(key, false);

        AssertionError thrown = assertThrows(
            AssertionError.class,
           () -> MerklePatriciaTree.prove(root, keyNibbles, proofs)
        );

        assertTrue(thrown.getMessage().contains("invalid MPT proof"));
    }

    @Test
    @Order(6)
    public void proveCanNotFindChildrenNode() {
        ArrayList<MPTNode> proofs = new ArrayList<MPTNode>(5);
        proofs.add(new MPTNode(HexConverter.hexStringToByteArray("80ffff801ee4115c4d5894ff9b5e6bb83611e2a8a8503482fe35a3f086d500fd1f111961806fe86874420b0da66e64aceb6faf0923bcfcac83ab77a12fcaf53915fe005dbe807ea888eb72a4703a6a2049ee5f8a4b57efb9ff127c33f4fc0a1b00f36f2ec6d080415256d8937fed4618e92d39ed6a96f986e4303ca3689b33c59ade227af7909a806af5c4f477504b627b1af9c106960aefe0e0a0632386b542897157486e0145a480142d00923c7e321f47d9eb0918514f983f9916c6db8c952d36fa5366da20ce9a800eb754c27d6302344f80fc4f785eae09c7c6acf58ee0ebddbd2f1755eb37a7de80487b2dc7bef5a919013bf34dfca66bbaae571ebedbdb0eeb0c8149dfa0d3545c805ef8441d86999ada141d6e18656a9243ceb6fadf27dc3c600d65d64488232c04800d7b7c347d9d129a1c9596f6ce1b8b1adf0d408acf677571a198c3eda6ad9b6280b54561a0ca6f56c484ed434f3cdd323985e74088f456e6c32ed47bd65dd7269f8074b8fd9e7670794f9cba3bdf1ac05b9104da6d27a42113f2f3d95a9ee90dc0298093096e5243ed57428d45ef91c5fcf8ff06179628488202e5d4864867b9674be280b911d65bff5f4ce6c0520d83d55b8ee182c7e87664f67664849276b731b4511780aa70f608bff62a7658840159c5f90224dd41de9b5a2232c2d2910e6179fb707f80a50e07dd134bf95fc2ff5676b0ec5e34d4803f0a679615405bf25cc87394fd5f")));
        proofs.add(new MPTNode(HexConverter.hexStringToByteArray("80499c808195b6400caeaaceee2debc277ceadf67ad2af2ddd02b815476ad91d303076c580fc6822539a4dd76f5bd08ebce76152f9043fe167826db92c7569e43c16d675af80f26e60648c101cd4e706c69caa588a40c5215c052e849fc410681277e12c0e5b80a108353045683e792e050af2eda268c860fbcd757d59dd5c194c6c726308459d80102c09e404e12ce2a6e71e214ea009053358e7f3de223457de265a6faab6e454803f6f047a1cb4d78714bcb64168513cca61d3120e8c0f5c30affb7393620f84b480623848e735dd707e95d569e06cbca4c4437a925d0887bfd1ce67b035d44aec80")));
        proofs.add(new MPTNode(HexConverter.hexStringToByteArray("9eaa394eea5630e07c48ae0c9558cef7299f80da681d5efb680bef057a58ffbaa7551590425911dc0b272c9da47fcde38f3fbe807da0cb76c07e5a74b3303c95a56cf06da88afdf25c39db028498d8e8cd2287694c5f0684a022a34dd8bfa2baaf44f172b7100401802f965a5e16a4a150ab5c9bf54c8a46eb01b020f026b3f608f2f3b65a219be1e3804ce3f5738277bd4abdf453f58df9c2da09cc53dee7bff7471ed6d2ed6bd26ee180f0ce00fa24fd38f8a6f4f65268bf304215733f9ca85d708581915983a1240342807bf5646da3793f6a01641f0b3808f1eb1959dcac33a5cfcea14ae652e8cfba864c5f021aab032aaa6e946ca50ad39ab666030401705f09cce9c888469bb1a0dceaa129672ef8287420706f6c6b61646f74")));
        proofs.add(new MPTNode(HexConverter.hexStringToByteArray("800404545ea5c1b19ab7a04f536c519aca4983ac1011094400545e98fdbe9ce6c55837576c60c7af38501001000000")));

        byte[] key = HexConverter.hexStringToByteArray("25aa394eea5630e07c48ae0c9558cef7");
        byte[] root = HexConverter.hexStringToByteArray("c40efd7458262583b22ddcb17b434a40b802116ed24bf6cdd0dbce4face6126e");

        Nibbles keyNibbles = new Nibbles(key, false);

        AssertionError thrown = assertThrows(
            AssertionError.class,
           () -> MerklePatriciaTree.prove(root, keyNibbles, proofs)
        );

        assertTrue(thrown.getMessage().contains("invalid MPT proof"));
    }

    @Test
    @Order(7)
    public void proveFailMismatchNibbleOnLeaf() {
        ArrayList<MPTNode> proofs = new ArrayList<MPTNode>(5);
        proofs.add(new MPTNode(HexConverter.hexStringToByteArray("80499c808195b6400caeaaceee2debc277ceadf67ad2af2ddd02b815476ad91d303076c580fc6822539a4dd76f5bd08ebce76152f9043fe167826db92c7569e43c16d675af80f26e60648c101cd4e706c69caa588a40c5215c052e849fc410681277e12c0e5b80a108353045683e792e050af2eda268c860fbcd757d59dd5c194c6c726308459d80102c09e404e12ce2a6e71e214ea009053358e7f3de223457de265a6faab6e454803f6f047a1cb4d78714bcb64168513cca61d3120e8c0f5c30affb7393620f84b480623848e735dd707e95d569e06cbca4c4437a925d0887bfd1ce67b035d44aec80")));
        proofs.add(new MPTNode(HexConverter.hexStringToByteArray("80ffff801ee4115c4d5894ff9b5e6bb83611e2a8a8503482fe35a3f086d500fd1f111961806fe86874420b0da66e64aceb6faf0923bcfcac83ab77a12fcaf53915fe005dbe807ea888eb72a4703a6a2049ee5f8a4b57efb9ff127c33f4fc0a1b00f36f2ec6d080415256d8937fed4618e92d39ed6a96f986e4303ca3689b33c59ade227af7909a806af5c4f477504b627b1af9c106960aefe0e0a0632386b542897157486e0145a480142d00923c7e321f47d9eb0918514f983f9916c6db8c952d36fa5366da20ce9a800eb754c27d6302344f80fc4f785eae09c7c6acf58ee0ebddbd2f1755eb37a7de80487b2dc7bef5a919013bf34dfca66bbaae571ebedbdb0eeb0c8149dfa0d3545c805ef8441d86999ada141d6e18656a9243ceb6fadf27dc3c600d65d64488232c04800d7b7c347d9d129a1c9596f6ce1b8b1adf0d408acf677571a198c3eda6ad9b6280b54561a0ca6f56c484ed434f3cdd323985e74088f456e6c32ed47bd65dd7269f8074b8fd9e7670794f9cba3bdf1ac05b9104da6d27a42113f2f3d95a9ee90dc0298093096e5243ed57428d45ef91c5fcf8ff06179628488202e5d4864867b9674be280b911d65bff5f4ce6c0520d83d55b8ee182c7e87664f67664849276b731b4511780aa70f608bff62a7658840159c5f90224dd41de9b5a2232c2d2910e6179fb707f80a50e07dd134bf95fc2ff5676b0ec5e34d4803f0a679615405bf25cc87394fd5f")));
        proofs.add(new MPTNode(HexConverter.hexStringToByteArray("9eaa394eea5630e07c48ae0c9558cef7299f80da681d5efb680bef057a58ffbaa7551590425911dc0b272c9da47fcde38f3fbe807da0cb76c07e5a74b3303c95a56cf06da88afdf25c39db028498d8e8cd2287694c5f0684a022a34dd8bfa2baaf44f172b7100401802f965a5e16a4a150ab5c9bf54c8a46eb01b020f026b3f608f2f3b65a219be1e3804ce3f5738277bd4abdf453f58df9c2da09cc53dee7bff7471ed6d2ed6bd26ee180f0ce00fa24fd38f8a6f4f65268bf304215733f9ca85d708581915983a1240342807bf5646da3793f6a01641f0b3808f1eb1959dcac33a5cfcea14ae652e8cfba864c5f021aab032aaa6e946ca50ad39ab666030401705f09cce9c888469bb1a0dceaa129672ef8287420706f6c6b61646f74")));
        proofs.add(new MPTNode(HexConverter.hexStringToByteArray("800404545ea5c1b19ab7a04f536c519aca4983ac1011094400545e98fdbe9ce6c55837576c60c7af38501001000000")));

        byte[] key = HexConverter.hexStringToByteArray("26aa394eea5630e07c48ae0c9558cef702a5c1b19ab7a04f536c519aca4983ad");
        byte[] root = HexConverter.hexStringToByteArray("c40efd7458262583b22ddcb17b434a40b802116ed24bf6cdd0dbce4face6126e");

        Nibbles keyNibbles = new Nibbles(key, false);

        AssertionError thrown = assertThrows(
            AssertionError.class,
           () -> MerklePatriciaTree.prove(root, keyNibbles, proofs)
        );

        assertTrue(thrown.getMessage().contains("MPT mismatch nibbles on leaf node"));
    }
}