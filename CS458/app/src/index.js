import Web3 from "web3";
import priceCoinArtifact from "../../build/contracts/PriceCoin.json";
import priceExchangeArtifact from "../../build/contracts/PriceExchange.json";

const App = {
  web3: null,
  account: null,
  coin: null,
  exchange: null,

  start: async function() {
    const { web3 } = this;

    try {
      const networkId = await web3.eth.net.getId();
      const deployedNetworkCoin = priceCoinArtifact.networks[networkId];
      const deployedNetworkExchange = priceExchangeArtifact.networks[networkId];
      
      this.exchange = new web3.eth.Contract(
        priceExchangeArtifact.abi,
        deployedNetworkExchange.address
      );

      // get accounts
      const accounts = await web3.eth.getAccounts();
      this.account = accounts[0];

      this.refreshBalance();
    } catch (error) {
      console.error("Could not connect to contract or chain.");
    }
  },

  refreshBalance: async function() {
    const balance = await this.exchange.methods.balanceOf(this.account).call();

    const balanceElement = document.getElementsByClassName("balance")[0];
    balanceElement.innerHTML = balance;
  },

  buyPrice: async function() {
    const amount = document.getElementById("buyAmount").value;

    this.setStatus("Executing transaction, please accept MetaMask confirmation.");

    console.log(await this.exchange.methods.buyPrice()
      .send({from: this.account, value: Web3.utils.toWei(amount, "ether")}));

    this.setStatus("Transaction complete!");
    this.refreshBalance();
  },

  sellPrice: async function() {
    const amount = document.getElementById("sellAmount").value;

    this.setStatus("Executing transaction, please accept MetaMask confirmation.");

    console.log(await this.exchange.methods.sellPrice(this.account, amount).send({ from: this.account}));

    this.setStatus("Transaction complete!");
    this.refreshBalance();
  },

  play: async function() {
    const betAmount = document.getElementById("wager").value;
    const leeway = document.getElementById("leeway").value;
    const guess = document.getElementById("guess").value;

    const results = await this.exchange.methods.play(this.account, betAmount, guess, leeway).send({from: this.account});
    const won = results["events"]["Outcome"]["returnValues"][0]; // Boolean - whether user won or not.
    const payout = results["events"]["Outcome"]["returnValues"][1]; // Payout amount 
    const randomNum = results["events"]["Outcome"]["returnValues"][2] // Random number that was generated.
    console.log(results);

    let outcome = document.getElementById("outcome");
    outcome.innerHTML = won ? "You won " + payout + " PRC!": "You lost " + betAmount + " PRC!";

    let number = document.getElementById("number");
    number.innerHTML = "The random number was: " + randomNum;

    this.refreshBalance();
  },

  setStatus: function(message) {
    const status = document.getElementById("status");
    status.innerHTML = message;
  },
  updateWin: async function(){
    var leeway = $("#leeway").val();
    var wager = $("#wager").val();
    const win = await this.exchange.methods.getPayout2(wager,leeway);
    console.log(win);
    return win;
  }
};

window.App = App;

window.addEventListener("load", function() {
  if (window.ethereum) {
    // use MetaMask's provider
    App.web3 = new Web3(window.ethereum);
    window.ethereum.enable(); // get permission to access accounts
  } else {
    console.warn(
      "No web3 detected. Falling back to http://127.0.0.1:8545. You should remove this fallback when you deploy live",
    );
    // fallback - use your fallback strategy (local node / hosted node + in-dapp id mgmt / fail)
    App.web3 = new Web3(
      new Web3.providers.HttpProvider("http://127.0.0.1:9545"),
    );
  }

  App.start();
});
export default App;

$( document ).ready(function() {

  $(document).on( 'click','#flexCheckChecked', async function () { 
      $("#slider-row").toggle() 
      
      updateWinningNumbers()
  });
  
  $(document).on( 'change','#guess', async function () { 
      updateWinningNumbers()
  });
  
  
  $(document).on( 'change','#rangeValue1', async function () { 
      updateWinningNumbers()
  });
  $(document).on( 'change','#wager', async function () { 
      updateWinningNumbers()
  });
  
  $(document).on( 'click','#slider-row', async function () { 
     updateWinningNumbers()
  });
  $(document).on( 'click','#slider-row-1', async function () { 
      updateWinningNumbers()
   });
  
  
  $(document).on( 'click','#send-button', async function () { 
      //console.log(App.account)
  
   });
  
  
  
  
  async function updateWinningNumbers(){
      var guess = $("#guess").val();
      var leeway = $("#leeway").val();
      var numGuess = Number(guess);
      var numLeeway = Number(leeway);
      var min = numGuess - numLeeway;
      var wager = Number($("#wager").val())
      if(min<1){
          min+=100;
      }
      var max = numGuess + numLeeway;
      if(max>100){
          max-=100;
      }
      $("#outcome").text("");
      $("#number").text("");
      if(leeway !== '0'){
          var odds = ((100-(leeway*2+1))/(leeway*2+1)).toFixed(2);
          var retOdds = (((100-(leeway*2+1))/(leeway*2+1)) - 0.05 * ((100-(leeway*2+1))/(leeway*2+1))).toFixed(2)
      $("#win-con").show()
      $("#num-range").text(`${min} - ${max}`)
      $("#win-odds").text(`${leeway*2+1}% or ${odds}:1`);
      $("#return-odds").text(`${retOdds}:1 [aka ${retOdds} coins profit for every 1 coin bet]`)
      //var range = SafeMath.safeAdd(SafeMath.safeMule(leeway, 2), 1);
      //var odds = SafeMath.safeDiv(SafeMath.safeMule(SafeMath.safeSub(100, range), Math.pow(10,18)), range)
      $("#return-win").text(`${(retOdds * wager).toFixed(3)}`);

  
      } else{
          $("#win-con").hide()
          $("#num-range").text(`${numGuess}`)
          $("#win-odds").text("1% or 99.00:1");
          $("#return-odds").text(`80.00:1 [aka 80.00 coins profit for every 1 coin bet]`)

          $("#return-win").text(`${(80 * wager).toFixed(3)}`);

      }
  
  }
  });