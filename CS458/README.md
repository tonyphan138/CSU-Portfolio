# The Price is Right DApp

### Team Members:
* Chris MacKenzie
* Ian Taylor
* Tony Phan
* Derek McCracken

### Project Summary:
On the front end, the user will have the choice 1-100, 1-1000, 1-10000 for their “guessing bracket” once their bracket has been chosen the user can select a random number in this range to place their bet. After their guess has been confirmed their chosen number will be compared against our randomly generated number, the user will also be given some leeway with their bet. For example, if the user choses 1-100 and guesses 2, while the answer is 99, 2 is within 5 points with a wraparound so the user would receive their initial bet and a reward.

### Project Motivation:
For this dApp, we have chosen to adapt a form of The Price Is Right to the blockchain. Most decentralized applications deal with finances in some form, so this betting game would be no different.

### Complexity:
This project will be moderately complex. We will need to hold the users money in a type of escrow, we will need to come up with a random number, and we will need to connect the GUI to the contract.

### Project Goals:
Build a betting application where a user tries to guess a random number and wins if they are within the specified range. The front end would allow the user to wager their money, select the range they would like to guess within, and show how much they stand to win, and whether or not they won in the end.

### Expected Schedule:
* Have contract working by Wednesday, April 20
* Have Web GUI running with contract by Monday, April 25
* Write Final Report and Presentation Slides written by Friday, April 29th
* Record Presentation video From April 29 - May 1
* Present on May 3rd or May 5th

### Workload Distribution:
* Ian Taylor: Solidity Contract & Final Report
* Chris MacKenzie: Web GUI & Final Report
* Tony Phan: Contract & Final Report 
* Derek McCracken: Web GUI & Final Report

### How to:
Use NPM to install packages and truffle using, "npm install truffle -g"
then lauch the local blockchain using "truffle develop"
within develop mode use migrate to compile and migrate the ABI to the contract.
within /app, use npm run dev to lauch the server on localhost:PORT.
Then connect metamask to the local chain, then upload a private key generated with truffle.
