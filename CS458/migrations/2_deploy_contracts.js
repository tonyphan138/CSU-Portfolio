const PriceCoin = artifacts.require("PriceCoin");
const PriceExchange = artifacts.require("PriceExchange");

module.exports = function(deployer) {
  deployer.deploy(PriceCoin);
  deployer.link(PriceCoin, PriceExchange);
  deployer.deploy(PriceExchange);
};