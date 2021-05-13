// Code generated - DO NOT EDIT.
// This file is a generated binding and any manual changes will be lost.

package binding

import (
	"math/big"
	"strings"

	ethereum "github.com/ethereum/go-ethereum"
	"github.com/ethereum/go-ethereum/accounts/abi"
	"github.com/ethereum/go-ethereum/accounts/abi/bind"
	"github.com/ethereum/go-ethereum/common"
	"github.com/ethereum/go-ethereum/core/types"
	"github.com/ethereum/go-ethereum/event"
)

// Reference imports to suppress errors if they are not otherwise used.
var (
	_ = big.NewInt
	_ = strings.NewReader
	_ = ethereum.NotFound
	_ = bind.Bind
	_ = common.Big1
	_ = types.BloomLookup
	_ = event.NewSubscription
)

// IBMCLinkStats is an auto generated low-level Go binding around an user-defined struct.
type IBMCLinkStats struct {
	RxSeq            *big.Int
	TxSeq            *big.Int
	Verifier         IBMCVerifierStatus
	Relays           []IBMCRelayStats
	RelayIdx         *big.Int
	RotateHeight     *big.Int
	RotateTerm       *big.Int
	DelayLimit       *big.Int
	MaxAggregation   *big.Int
	RxHeightSrc      *big.Int
	RxHeight         *big.Int
	BlockIntervalSrc *big.Int
	BlockIntervalDst *big.Int
	CurrentHeight    *big.Int
}

// IBMCRelayStats is an auto generated low-level Go binding around an user-defined struct.
type IBMCRelayStats struct {
	Addr       common.Address
	BlockCount *big.Int
	MsgCount   *big.Int
}

// IBMCVerifierStatus is an auto generated low-level Go binding around an user-defined struct.
type IBMCVerifierStatus struct {
	HeightMTA  *big.Int
	OffsetMTA  *big.Int
	LastHeight *big.Int
}

// BMCABI is the input ABI used to generate the binding from.
const BMCABI = "[{\"inputs\":[{\"internalType\":\"string\",\"name\":\"_network\",\"type\":\"string\"}],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"_net\",\"type\":\"string\"},{\"internalType\":\"address\",\"name\":\"_addr\",\"type\":\"address\"}],\"name\":\"addVerifier\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"_link\",\"type\":\"string\"},{\"internalType\":\"address[]\",\"name\":\"_addr\",\"type\":\"address[]\"}],\"name\":\"addRelay\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"_addr\",\"type\":\"address\"},{\"internalType\":\"string\",\"name\":\"_desc\",\"type\":\"string\"}],\"name\":\"addRelayer\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"_link\",\"type\":\"string\"}],\"name\":\"addLink\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"_link\",\"type\":\"string\"}],\"name\":\"getStatus\",\"outputs\":[{\"components\":[{\"internalType\":\"uint256\",\"name\":\"rxSeq\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"txSeq\",\"type\":\"uint256\"},{\"components\":[{\"internalType\":\"uint256\",\"name\":\"heightMTA\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"offsetMTA\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"lastHeight\",\"type\":\"uint256\"}],\"internalType\":\"structIBMC.VerifierStatus\",\"name\":\"verifier\",\"type\":\"tuple\"},{\"components\":[{\"internalType\":\"address\",\"name\":\"addr\",\"type\":\"address\"},{\"internalType\":\"uint256\",\"name\":\"blockCount\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"msgCount\",\"type\":\"uint256\"}],\"internalType\":\"structIBMC.RelayStats[]\",\"name\":\"relays\",\"type\":\"tuple[]\"},{\"internalType\":\"uint256\",\"name\":\"relayIdx\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"rotateHeight\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"rotateTerm\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"delayLimit\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"maxAggregation\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"rxHeightSrc\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"rxHeight\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"blockIntervalSrc\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"blockIntervalDst\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"currentHeight\",\"type\":\"uint256\"}],\"internalType\":\"structIBMC.LinkStats\",\"name\":\"\",\"type\":\"tuple\"}],\"stateMutability\":\"view\",\"type\":\"function\",\"constant\":true},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"_prev\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"_msg\",\"type\":\"string\"}],\"name\":\"handleRelayMessage\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"

// BMC is an auto generated Go binding around an Ethereum contract.
type BMC struct {
	BMCCaller     // Read-only binding to the contract
	BMCTransactor // Write-only binding to the contract
	BMCFilterer   // Log filterer for contract events
}

// BMCCaller is an auto generated read-only Go binding around an Ethereum contract.
type BMCCaller struct {
	contract *bind.BoundContract // Generic contract wrapper for the low level calls
}

// BMCTransactor is an auto generated write-only Go binding around an Ethereum contract.
type BMCTransactor struct {
	contract *bind.BoundContract // Generic contract wrapper for the low level calls
}

// BMCFilterer is an auto generated log filtering Go binding around an Ethereum contract events.
type BMCFilterer struct {
	contract *bind.BoundContract // Generic contract wrapper for the low level calls
}

// BMCSession is an auto generated Go binding around an Ethereum contract,
// with pre-set call and transact options.
type BMCSession struct {
	Contract     *BMC              // Generic contract binding to set the session for
	CallOpts     bind.CallOpts     // Call options to use throughout this session
	TransactOpts bind.TransactOpts // Transaction auth options to use throughout this session
}

// BMCCallerSession is an auto generated read-only Go binding around an Ethereum contract,
// with pre-set call options.
type BMCCallerSession struct {
	Contract *BMCCaller    // Generic contract caller binding to set the session for
	CallOpts bind.CallOpts // Call options to use throughout this session
}

// BMCTransactorSession is an auto generated write-only Go binding around an Ethereum contract,
// with pre-set transact options.
type BMCTransactorSession struct {
	Contract     *BMCTransactor    // Generic contract transactor binding to set the session for
	TransactOpts bind.TransactOpts // Transaction auth options to use throughout this session
}

// BMCRaw is an auto generated low-level Go binding around an Ethereum contract.
type BMCRaw struct {
	Contract *BMC // Generic contract binding to access the raw methods on
}

// BMCCallerRaw is an auto generated low-level read-only Go binding around an Ethereum contract.
type BMCCallerRaw struct {
	Contract *BMCCaller // Generic read-only contract binding to access the raw methods on
}

// BMCTransactorRaw is an auto generated low-level write-only Go binding around an Ethereum contract.
type BMCTransactorRaw struct {
	Contract *BMCTransactor // Generic write-only contract binding to access the raw methods on
}

// NewBMC creates a new instance of BMC, bound to a specific deployed contract.
func NewBMC(address common.Address, backend bind.ContractBackend) (*BMC, error) {
	contract, err := bindBMC(address, backend, backend, backend)
	if err != nil {
		return nil, err
	}
	return &BMC{BMCCaller: BMCCaller{contract: contract}, BMCTransactor: BMCTransactor{contract: contract}, BMCFilterer: BMCFilterer{contract: contract}}, nil
}

// NewBMCCaller creates a new read-only instance of BMC, bound to a specific deployed contract.
func NewBMCCaller(address common.Address, caller bind.ContractCaller) (*BMCCaller, error) {
	contract, err := bindBMC(address, caller, nil, nil)
	if err != nil {
		return nil, err
	}
	return &BMCCaller{contract: contract}, nil
}

// NewBMCTransactor creates a new write-only instance of BMC, bound to a specific deployed contract.
func NewBMCTransactor(address common.Address, transactor bind.ContractTransactor) (*BMCTransactor, error) {
	contract, err := bindBMC(address, nil, transactor, nil)
	if err != nil {
		return nil, err
	}
	return &BMCTransactor{contract: contract}, nil
}

// NewBMCFilterer creates a new log filterer instance of BMC, bound to a specific deployed contract.
func NewBMCFilterer(address common.Address, filterer bind.ContractFilterer) (*BMCFilterer, error) {
	contract, err := bindBMC(address, nil, nil, filterer)
	if err != nil {
		return nil, err
	}
	return &BMCFilterer{contract: contract}, nil
}

// bindBMC binds a generic wrapper to an already deployed contract.
func bindBMC(address common.Address, caller bind.ContractCaller, transactor bind.ContractTransactor, filterer bind.ContractFilterer) (*bind.BoundContract, error) {
	parsed, err := abi.JSON(strings.NewReader(BMCABI))
	if err != nil {
		return nil, err
	}
	return bind.NewBoundContract(address, parsed, caller, transactor, filterer), nil
}

// Call invokes the (constant) contract method with params as input values and
// sets the output to result. The result type might be a single field for simple
// returns, a slice of interfaces for anonymous returns and a struct for named
// returns.
func (_BMC *BMCRaw) Call(opts *bind.CallOpts, result *[]interface{}, method string, params ...interface{}) error {
	return _BMC.Contract.BMCCaller.contract.Call(opts, result, method, params...)
}

// Transfer initiates a plain transaction to move funds to the contract, calling
// its default method if one is available.
func (_BMC *BMCRaw) Transfer(opts *bind.TransactOpts) (*types.Transaction, error) {
	return _BMC.Contract.BMCTransactor.contract.Transfer(opts)
}

// Transact invokes the (paid) contract method with params as input values.
func (_BMC *BMCRaw) Transact(opts *bind.TransactOpts, method string, params ...interface{}) (*types.Transaction, error) {
	return _BMC.Contract.BMCTransactor.contract.Transact(opts, method, params...)
}

// Call invokes the (constant) contract method with params as input values and
// sets the output to result. The result type might be a single field for simple
// returns, a slice of interfaces for anonymous returns and a struct for named
// returns.
func (_BMC *BMCCallerRaw) Call(opts *bind.CallOpts, result *[]interface{}, method string, params ...interface{}) error {
	return _BMC.Contract.contract.Call(opts, result, method, params...)
}

// Transfer initiates a plain transaction to move funds to the contract, calling
// its default method if one is available.
func (_BMC *BMCTransactorRaw) Transfer(opts *bind.TransactOpts) (*types.Transaction, error) {
	return _BMC.Contract.contract.Transfer(opts)
}

// Transact invokes the (paid) contract method with params as input values.
func (_BMC *BMCTransactorRaw) Transact(opts *bind.TransactOpts, method string, params ...interface{}) (*types.Transaction, error) {
	return _BMC.Contract.contract.Transact(opts, method, params...)
}

// GetStatus is a free data retrieval call binding the contract method 0x22b05ed2.
//
// Solidity: function getStatus(string _link) view returns((uint256,uint256,(uint256,uint256,uint256),(address,uint256,uint256)[],uint256,uint256,uint256,uint256,uint256,uint256,uint256,uint256,uint256,uint256))
func (_BMC *BMCCaller) GetStatus(opts *bind.CallOpts, _link string) (IBMCLinkStats, error) {
	var out []interface{}
	err := _BMC.contract.Call(opts, &out, "getStatus", _link)

	if err != nil {
		return *new(IBMCLinkStats), err
	}

	out0 := *abi.ConvertType(out[0], new(IBMCLinkStats)).(*IBMCLinkStats)

	return out0, err

}

// GetStatus is a free data retrieval call binding the contract method 0x22b05ed2.
//
// Solidity: function getStatus(string _link) view returns((uint256,uint256,(uint256,uint256,uint256),(address,uint256,uint256)[],uint256,uint256,uint256,uint256,uint256,uint256,uint256,uint256,uint256,uint256))
func (_BMC *BMCSession) GetStatus(_link string) (IBMCLinkStats, error) {
	return _BMC.Contract.GetStatus(&_BMC.CallOpts, _link)
}

// GetStatus is a free data retrieval call binding the contract method 0x22b05ed2.
//
// Solidity: function getStatus(string _link) view returns((uint256,uint256,(uint256,uint256,uint256),(address,uint256,uint256)[],uint256,uint256,uint256,uint256,uint256,uint256,uint256,uint256,uint256,uint256))
func (_BMC *BMCCallerSession) GetStatus(_link string) (IBMCLinkStats, error) {
	return _BMC.Contract.GetStatus(&_BMC.CallOpts, _link)
}

// AddLink is a paid mutator transaction binding the contract method 0x22a618fa.
//
// Solidity: function addLink(string _link) returns()
func (_BMC *BMCTransactor) AddLink(opts *bind.TransactOpts, _link string) (*types.Transaction, error) {
	return _BMC.contract.Transact(opts, "addLink", _link)
}

// AddLink is a paid mutator transaction binding the contract method 0x22a618fa.
//
// Solidity: function addLink(string _link) returns()
func (_BMC *BMCSession) AddLink(_link string) (*types.Transaction, error) {
	return _BMC.Contract.AddLink(&_BMC.TransactOpts, _link)
}

// AddLink is a paid mutator transaction binding the contract method 0x22a618fa.
//
// Solidity: function addLink(string _link) returns()
func (_BMC *BMCTransactorSession) AddLink(_link string) (*types.Transaction, error) {
	return _BMC.Contract.AddLink(&_BMC.TransactOpts, _link)
}

// AddRelay is a paid mutator transaction binding the contract method 0x0748ea7a.
//
// Solidity: function addRelay(string _link, address[] _addr) returns()
func (_BMC *BMCTransactor) AddRelay(opts *bind.TransactOpts, _link string, _addr []common.Address) (*types.Transaction, error) {
	return _BMC.contract.Transact(opts, "addRelay", _link, _addr)
}

// AddRelay is a paid mutator transaction binding the contract method 0x0748ea7a.
//
// Solidity: function addRelay(string _link, address[] _addr) returns()
func (_BMC *BMCSession) AddRelay(_link string, _addr []common.Address) (*types.Transaction, error) {
	return _BMC.Contract.AddRelay(&_BMC.TransactOpts, _link, _addr)
}

// AddRelay is a paid mutator transaction binding the contract method 0x0748ea7a.
//
// Solidity: function addRelay(string _link, address[] _addr) returns()
func (_BMC *BMCTransactorSession) AddRelay(_link string, _addr []common.Address) (*types.Transaction, error) {
	return _BMC.Contract.AddRelay(&_BMC.TransactOpts, _link, _addr)
}

// AddRelayer is a paid mutator transaction binding the contract method 0x513a4c82.
//
// Solidity: function addRelayer(address _addr, string _desc) returns()
func (_BMC *BMCTransactor) AddRelayer(opts *bind.TransactOpts, _addr common.Address, _desc string) (*types.Transaction, error) {
	return _BMC.contract.Transact(opts, "addRelayer", _addr, _desc)
}

// AddRelayer is a paid mutator transaction binding the contract method 0x513a4c82.
//
// Solidity: function addRelayer(address _addr, string _desc) returns()
func (_BMC *BMCSession) AddRelayer(_addr common.Address, _desc string) (*types.Transaction, error) {
	return _BMC.Contract.AddRelayer(&_BMC.TransactOpts, _addr, _desc)
}

// AddRelayer is a paid mutator transaction binding the contract method 0x513a4c82.
//
// Solidity: function addRelayer(address _addr, string _desc) returns()
func (_BMC *BMCTransactorSession) AddRelayer(_addr common.Address, _desc string) (*types.Transaction, error) {
	return _BMC.Contract.AddRelayer(&_BMC.TransactOpts, _addr, _desc)
}

// AddVerifier is a paid mutator transaction binding the contract method 0x76b503c3.
//
// Solidity: function addVerifier(string _net, address _addr) returns()
func (_BMC *BMCTransactor) AddVerifier(opts *bind.TransactOpts, _net string, _addr common.Address) (*types.Transaction, error) {
	return _BMC.contract.Transact(opts, "addVerifier", _net, _addr)
}

// AddVerifier is a paid mutator transaction binding the contract method 0x76b503c3.
//
// Solidity: function addVerifier(string _net, address _addr) returns()
func (_BMC *BMCSession) AddVerifier(_net string, _addr common.Address) (*types.Transaction, error) {
	return _BMC.Contract.AddVerifier(&_BMC.TransactOpts, _net, _addr)
}

// AddVerifier is a paid mutator transaction binding the contract method 0x76b503c3.
//
// Solidity: function addVerifier(string _net, address _addr) returns()
func (_BMC *BMCTransactorSession) AddVerifier(_net string, _addr common.Address) (*types.Transaction, error) {
	return _BMC.Contract.AddVerifier(&_BMC.TransactOpts, _net, _addr)
}

// HandleRelayMessage is a paid mutator transaction binding the contract method 0x6f4779cc.
//
// Solidity: function handleRelayMessage(string _prev, string _msg) returns()
func (_BMC *BMCTransactor) HandleRelayMessage(opts *bind.TransactOpts, _prev string, _msg string) (*types.Transaction, error) {
	return _BMC.contract.Transact(opts, "handleRelayMessage", _prev, _msg)
}

// HandleRelayMessage is a paid mutator transaction binding the contract method 0x6f4779cc.
//
// Solidity: function handleRelayMessage(string _prev, string _msg) returns()
func (_BMC *BMCSession) HandleRelayMessage(_prev string, _msg string) (*types.Transaction, error) {
	return _BMC.Contract.HandleRelayMessage(&_BMC.TransactOpts, _prev, _msg)
}

// HandleRelayMessage is a paid mutator transaction binding the contract method 0x6f4779cc.
//
// Solidity: function handleRelayMessage(string _prev, string _msg) returns()
func (_BMC *BMCTransactorSession) HandleRelayMessage(_prev string, _msg string) (*types.Transaction, error) {
	return _BMC.Contract.HandleRelayMessage(&_BMC.TransactOpts, _prev, _msg)
}
