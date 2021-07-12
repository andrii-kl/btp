package icon

import (
	"fmt"
	"testing"

	"github.com/icon-project/btp/chain"
	"github.com/icon-project/btp/common/log"
	"github.com/stretchr/testify/assert"
)

func TestReceiver_ReceiveLoop(t *testing.T) {
	t.Run("should monitor from the given height", func(t *testing.T) {
		t.Skip("This is for manual run")
		r := NewReceiver("btp://0x53.icon/cxdc2a468aada7a4826176e87ae72d6ee24c50df0a", "btp://0x507.pra/0x6a436465184fA9b0b5f20fbeFADaF83CaC466ACD", "https://sejong.net.solidwallet.io/api/v3/icon_dex", nil, log.New())
		err := r.ReceiveLoop(560733, 1, func(bu *chain.BlockUpdate, rps []*chain.ReceiptProof) {
			assert.EqualValues(t, bu.Height, 560733)
			assert.Equal(t, "0x6ee5f7dfc438f30818fe31351328bf3f84bb5791a77d1bba127fb7f7651f2b9f", fmt.Sprintf("0x%x", bu.BlockHash))
			assert.Equal(t, "0xf901bd0283088e5d8705c66fb357515295002ad7b8a8735bd42288a3536f84ef48ce3dc422daa0270495df6d85da73327b3f8325a2b1f1a2a56c55aec634404f1f46b543a769dca00d62cd556ad88c68cfc217ee5d58de14bd0de74d3dc05660a40d58753efc45d7a057b858ded1d7ccdd8ad8cebee01d7c6215e70ab90f14463084d48766de76d421f800a04901d63e276865e3f521de904daa1d1a8dc939e0989dbc800194b89df5d5c4f9a201002070482c1a0f0304410030886c3a1f100042a230689c522f188cc1c811a81c04b8eff8eda029edd8cf1ece7b28976cc8e545cfe93e55eb795388dfe3297c3c48a754c12deaf800a0a086ce7ac3c1950c2036186f951002476f20ebdb52aeecaef83bdee5bc44a8ebb8a7f8a5a0e9f536d238573c7221403cb7556abe727b25e97d7d19bc8c857b741c4fcc4eada01b36e9ff5b7b6a90d60e7e7668df097ca0f43cc897396e6d1b57634a7ffff581a0019768905b77979d96437fcfc2382f6bde9694799006d94e6932c9f7387c2d39a08e34656b341d36ab016e1dac35eafcb9d493075f40c02e617cf5065679336ef2a06496ce45c101862923051f8dd4cd5ddf8e608029672f5ab44191f71135ea0127",
				fmt.Sprintf("0x%x", bu.Header),
			)
			assert.Equal(t, "0xf907a9b901c0f901bd0283088e5d8705c66fb357515295002ad7b8a8735bd42288a3536f84ef48ce3dc422daa0270495df6d85da73327b3f8325a2b1f1a2a56c55aec634404f1f46b543a769dca00d62cd556ad88c68cfc217ee5d58de14bd0de74d3dc05660a40d58753efc45d7a057b858ded1d7ccdd8ad8cebee01d7c6215e70ab90f14463084d48766de76d421f800a04901d63e276865e3f521de904daa1d1a8dc939e0989dbc800194b89df5d5c4f9a201002070482c1a0f0304410030886c3a1f100042a230689c522f188cc1c811a81c04b8eff8eda029edd8cf1ece7b28976cc8e545cfe93e55eb795388dfe3297c3c48a754c12deaf800a0a086ce7ac3c1950c2036186f951002476f20ebdb52aeecaef83bdee5bc44a8ebb8a7f8a5a0e9f536d238573c7221403cb7556abe727b25e97d7d19bc8c857b741c4fcc4eada01b36e9ff5b7b6a90d60e7e7668df097ca0f43cc897396e6d1b57634a7ffff581a0019768905b77979d96437fcfc2382f6bde9694799006d94e6932c9f7387c2d39a08e34656b341d36ab016e1dac35eafcb9d493075f40c02e617cf5065679336ef2a06496ce45c101862923051f8dd4cd5ddf8e608029672f5ab44191f71135ea0127b905e1f905de00e201a03ada4f90c4f324224a7c54a4a5777e2dc9c629d9298928df6f49397acf033eaef905b7f84b8705c66fb37617a1b84137f006ff9265585a4e45d102cc7c2bae41fc37904d28f8887cf94e00a6ce85fc7571f873ce2bbe846e809115a5f95c6ad7e21a4daa77f078e40dea0f755adbf300f84b8705c66fb3761958b84112dd304b499e997e5854758dc372d75017842d4d424039509a6bb0359e36a538673c709012550407074b786711f0a96391d2b56ea5205ca3efecbbab6da6b0f401f84b8705c66fb3761a52b841bf1e793e6fa390ceae77d36110e6bc921955e5b0e751a801016dd49146fcceed1c1eb51b128655e3b8b4c34dbe31cce94943cc90cdf5aab48facf88ec1cfdc8b00f84b8705c66fb3763514b8410da02a11947f835e3c7dd4b66133daf032ec8c63ad53340292d709820b11b8892f855e67162c2dd394eddc4639f7187edb8fa70ab1909b3cd77d555cf92ca07000f84b8705c66fb3761c86b8414873d374c85312c8210dbf5c31c0058232882bae0c8358c4855610546a297e9120a6a0580ff55b909ab937f5cf9c3bfe8f64691e3a1eed50ce3446e0e75fb03701f84b8705c66fb37618d9b8419714dcdfbb974bcce996da28cdfe1249c486ef668f923b3a17e8e54ce08eaf3f5078a6b825185b592e4850127c627360b346673bc99070a72767e874b2a78f7a01f84b8705c66fb3761a22b841e7d810cd727d5fa321cbfb147d4c854bb9a3254d85881f92601224097f6d73ca1ad3bf305445981d191c3b7f349f7b25a567abef37538e1caab71df4ed5a973d01f84b8705c66fb3761996b841ddff929c23a764c1b1247e29e9c9d97d9f6f9f1e59dcc603c73b49ca9be7fdef311b075bf29a4f33c83d32b7dbe65a7e6efadfb9f0d921ba103455b576519eb001f84b8705c66fb37622beb841e6ffb3c09af9f8c52c9d597635b173e4c76c439dd59534e030d61d00240aa1cd0f7c944e28e47957b75119ed894d23639f7846cb6ee6fe187ed0345ec661420a01f84b8705c66fb3761f39b841ce6c7ab08833ec04dad5360ecbb4c861fa0db5f30a1bc38832ccff2339e6aab85758df0805c8f36a524014e58b57cba86abb393339a3258c5fb93508cd2b624400f84b8705c66fb3761a62b841b93c90a2cbc28d1be5fdebf70301647b3a86e986e2d03e3d250cb67d2fe8ced2484d10507c097a79e3175d12cf2673891f775e2a54e62760043362919532c3ea01f84b8705c66fb37627bcb841e40a474731ad3a3d4ccc12d170b648aba65c10acfbfcad9b52dfecfb5fa99dd43e4d0648c8fc6d0b8825f4ceaf478e0094d0a4394fc433a381427b05ad686fd200f84b8705c66fb3762c1db841a61ad1586b3c63d6f6c05cbdadc354b4127ad33e36238dfbc7893d3f29dddf99470ef001e70ee8e2aaa6975bc2a27d7f3834bd17ee2441edbb3798c0f90d8b7200f84b8705c66fb37619d6b8412c543e1ae5c8b9106cd58e53edd24def35f5bbf75a8dd5ee57a73b960abb008730442d2a4df539ad717bb52ae74220399c1f2051fb9972ff6a684859f1ff86af00f84b8705c66fb3761a63b841487986d56378e4b19c3b2ad2c04f05e8332c1c9c440e103c0cdbc16c2794261124115fec08c132daa27c015bae72b25ac6cfb327a95b6ccd36719b875736628f01f84b8705c66fb37619a6b8414c001acf02e4ab1ead4969d10cc8e357ffb2a4f0cf56b0876dbc5a373f1132a109a508fd12f309bf2b95e6e644ae039d02846011ac7f4f12e8d3e654009df72d01f84b8705c66fb3761777b841e708a0a89d31dd825df718eaaab211f7201312170c662ebc614c3229f30d2f3c1d81e408f4454c6af1ccf732b45272a926ec85ebbe63d3947c68a23a3682cdb001f84b8705c66fb37618e2b8416a650e64a20141e6ce8c1eebee8071a6be7384deb329993bd4ac6137d9183133406ce79cb8819b31312946b8031d2f3532cd7bed1f0e7f71239aa4e02cb4e88d00f84b8705c66fb3762452b841e60969ff8033e5442aa9f47a8cef8d795b4080e13cbf4945df92b42dd9bb0cf85a489d31c31473188bc908b06b491ecc8ae6aa399ed6af7d4552f4ab97c4e36801f800",
				fmt.Sprintf("0x%x", bu.Proof),
			)
			r.StopReceiveLoop()
		}, func() {})

		assert.NoError(t, err)
	})

	// t.Run("should build ReceiptProofs when events contains BMC SendMessage Event")
}
