package pra

import (
	stypes "github.com/centrifuge/go-substrate-rpc-client/v3/types"
)

const (
	DuplicateTransactionError = iota + 2000
	TransactionPoolOverflowError
	ExpiredTransactionError
	FutureTransactionError
	TransitionInterruptedError
	InvalidTransactionError
	InvalidQueryError
	InvalidResultError
	NoActiveContractError
	NotContractAddressError
	InvalidPatchDataError
	CommittedTransactionError
)

type Wallet interface {
	Sign(data []byte) ([]byte, error)
	Address() string
}

type RelayMessageParam struct {
	Prev string
	Msg  string
}

type RelayMessage struct {
	BlockUpdates  [][]byte
	BlockProof    []byte
	ReceiptProofs [][]byte
	//
	height              int64
	numberOfBlockUpdate int
	eventSequence       int64
	numberOfEvent       int
}

type SignedHeader struct {
	stypes.Header
	stypes.Justification
}

type BlockNotification struct {
	Header *stypes.Header
	Hash   SubstrateHash
	Height uint64
	Events *SubstateWithFrontierEventRecord
}

type ReadProof struct {
	At    SubstrateHash `json:"at"`
	Proof []string      `json:"proof"`
}

type TransactionHashParam struct {
	TxHash string
	Param  *RelayMessageParam
}
