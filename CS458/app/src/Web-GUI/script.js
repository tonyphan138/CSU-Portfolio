
$( document ).ready(function() {

$(document).on( 'click','#flexCheckChecked', function () { 
    $("#slider-row").toggle() 
    
    updateWinningNumbers()
});

$(document).on( 'change','#guess', function () { 
    updateWinningNumbers()
});


$(document).on( 'change','#rangeValue1', function () { 
    updateWinningNumbers()
});
$(document).on( 'change','#wager', function () { 
    updateWinningNumbers()
});

$(document).on( 'click','#slider-row', function () { 
   updateWinningNumbers()
});
$(document).on( 'click','#slider-row-1', function () { 
    updateWinningNumbers()
 });


$(document).on( 'click','#send-button', async function () { 
    //console.log(App.account)

 });




function updateWinningNumbers(){
    var guess = $("#guess").val();
    var leeway = $("#leeway").val();
    var numGuess = Number(guess);
    var numLeeway = Number(leeway);
    var min = numGuess - numLeeway;
    var wager = Number($("#wager").val())
    console.log(wager)
    if(min<1){
        min+=100;
    }
    var max = numGuess + numLeeway;
    if(max>100){
        max-=100;
    }
    if(leeway !== '0'){
        var odds = ((100-(leeway*2+1))/(leeway*2+1)).toFixed(2);
        var retOdds = (((100-(leeway*2+1))/(leeway*2+1)) - 0.05 * ((100-(leeway*2+1))/(leeway*2+1))).toFixed(2)
    $("#win-con").show()
    $("#num-range").text(`${min} - ${max}`)
    $("#win-odds").text(`${leeway*2+1}% or ${odds}:1`);
    $("#return-odds").text(`${retOdds}:1 [aka ${retOdds} coins profit for every 1 coin bet]`)
    $("#return-win").text(`${(retOdds*wager+wager).toFixed(3)}`);

    } else{
        $("#win-con").hide()
        $("#num-range").text(`${numGuess}`)
        $("#win-odds").text("1% or 99.00:1");
        $("#return-odds").text(`80.00:1 [aka 80.00 coins profit for every 1 coin bet]`)
        $("#return-win").text(`${(80*wager+wager).toFixed(3)}`);
    }

}







});