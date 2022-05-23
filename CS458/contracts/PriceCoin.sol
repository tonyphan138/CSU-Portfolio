// SPDX-License-Identifier: MIT
pragma solidity >=0.5.0;

import "@openzeppelin/contracts/utils/math/SafeMath.sol";

contract PriceCoin {
    string private _name;
    string private _symbol;
    uint8 public _decimals;
    uint256 private _totalSupply;
    address private _owner;

    uint256 public _rate = 500;

    mapping(address => uint256) public _balances;

    event Transfer(uint256 amount);
    event Mint(uint256 amount, address account);
    event Burn(uint256 amount, address account);

    constructor() {
        _name = "PriceCoin";
        _decimals = 18;
        _totalSupply = SafeMath.mul(1000, 10**18);
        _owner = msg.sender;
        _balances[_owner] = _totalSupply;
        emit Transfer(_totalSupply);
    }

    fallback() external payable {}

    receive() external payable {}

    function getTotalSupply() public view returns (uint256) {
        return _totalSupply;
    }

    function balanceOf(address addr) public view returns (uint256) {
        return SafeMath.div(_balances[addr], 10**_decimals);
    }
    function balanceOfEth(address addr) public view returns (uint256) {
        return _balances[addr] * _rate;
    }

    function transfer(address to, uint256 amount) public returns (bool) {
        require(
            _balances[msg.sender] >= amount,
            "Balance is smaller than amount."
        );
        _balances[msg.sender] -= amount;
        _balances[to] += amount;
        emit Transfer(amount);
        return true;
    }

    function _mint(address account, uint256 amount) internal {
        _totalSupply = SafeMath.add(_totalSupply, amount);
        _balances[account] = SafeMath.add(_balances[account], amount);
        emit Mint(amount, account);
    }

    function _burn(address account, uint256 amount) internal {
        _balances[account] = SafeMath.sub(_balances[account], amount);
        _totalSupply = SafeMath.sub(_totalSupply, amount);
        emit Burn(amount, account);
    }
}
