// SPDX-License-Identifier: MIT
pragma solidity >=0.5.0;

import "./PriceCoin.sol";
import "@openzeppelin/contracts/utils/math/SafeMath.sol";

contract PriceExchange is PriceCoin {
    uint256 private _randNonce;

    event Buy(uint256 priceRequested, uint256 etherPaid);
    event Sell(uint256 pricePaid, uint256 etherRequested);
    event Outcome(bool won, uint256 payout, uint256 randomNum);
    constructor() PriceCoin() {
        _randNonce = 0;
    }

    function buyPrice() external payable {
        require(msg.value >= 0, "You must send some ether.");
        uint256 priceRequested = SafeMath.mul(msg.value, _rate);
        _mint(msg.sender, priceRequested);
        emit Buy(priceRequested, msg.value);
    }

    function sellPrice(address payable _to, uint256 amount) external payable {
        uint256 priceToSell = SafeMath.mul(amount, 10**_decimals);
        uint256 etherRequested = SafeMath.div(priceToSell, _rate);

        require(_balances[_to] >= priceToSell, "You must hold an equal or lesser value of price coin to sell this much.");
        _burn(_to, priceToSell);
        (bool paid,) = _to.call{value: etherRequested }("");
        require(paid, "Transaction failed");
        emit Sell(priceToSell, etherRequested);
    }

    // Gets random number from 1-100 -- DETERMINISTIC (not completely random)
    function getRandomNumber() internal returns (int256) {
        uint256 randomNum = (uint256(keccak256(abi.encodePacked(block.timestamp, msg.sender, _randNonce++))) % 100) + 1;
        return int256(randomNum);
        // return 100; // used for testing
    }

    // Gets payout if number was within leeway. This method should also reimburse original bet amount.
    // Follows same logic seen in app/src/Web-GUI/script.js file
    function getPayout(uint256 betAmount, int256 leeway) internal view returns (uint256) {
        if (leeway == 0) return (80 * betAmount) + betAmount;
        
        uint256 range = SafeMath.add(SafeMath.mul(uint256(leeway), 2), 1);
        uint256 odds = SafeMath.div(SafeMath.mul(SafeMath.sub(100, range), 10**_decimals), range);
        uint256 retOdds = SafeMath.mul(SafeMath.sub(odds, SafeMath.div(odds, 20)), betAmount);
        return SafeMath.add(SafeMath.div(retOdds, 10**_decimals), betAmount); // + betAmount reimburses what was burned
    }

    // Returns whether guess was in the leeway range and the random number.
    function guessNumber(int256 guess, int256 leeway) internal returns(bool, uint256) {
        int256 upperBound = guess + leeway;
        int256 lowerBound = guess - leeway;

        bool upperWrap = upperBound > 100 ? true : false;
        bool lowerWrap = lowerBound < 1 ? true : false;

        // Update bounds if there is a wrap.
        upperBound = upperWrap ? (upperBound % 100) : upperBound;
        lowerBound = lowerWrap ? 100 + (lowerBound % 100) : lowerBound;
        
        int256 randomNum = getRandomNumber();

        // No wrap arounds
        if (!upperWrap && !lowerWrap) {
            return ((randomNum >= lowerBound && randomNum <= upperBound), uint256(randomNum));
        }
        else if (upperWrap && !lowerWrap) {
            return (((randomNum >= lowerBound) && (randomNum <= 100)) || (randomNum <= upperBound), uint256(randomNum));
        }
        else if (!upperWrap && lowerWrap) {
            return (((randomNum <= upperBound) && (randomNum >= 1)) || (randomNum >= lowerBound), uint256(randomNum));
        } 
        else { // Redundant if we limit leeway to 0-24
            return (true, uint256(randomNum));
        }
    }

    function play(address _to, uint256 betAmount, int256 guess, int256 leeway) external {
        require(guess >= 1 && guess <= 100, "Guess has to be between 1 and 100.");
        require(leeway >= 0 && leeway <= 24, "Leeway has to be between 0 and 24");

        uint256 priceToBet = SafeMath.mul(betAmount, 10**_decimals);
        require (_balances[_to] >= priceToBet,"You must hold an equal or lesser value of price coin to bet this much");

        // Burn before running to prevent re-entry attacks.
        _burn(_to, priceToBet);

        (bool won, uint256 randomNum) = guessNumber(guess, leeway); 

        if (won) {
            _mint(_to, getPayout(priceToBet, leeway));
        }
        emit Outcome(won, SafeMath.div(getPayout(priceToBet, leeway) - priceToBet, 10**_decimals), randomNum);
    }
}