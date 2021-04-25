let payment_form = $("#payment_information");


function handleFailure(res) {
    $("#payment_message").text(res["responseJSON"]["message"]);
}

function handlePaymentResult(res) {

    if (res["status"] === "success") {
        window.location.replace("confirm.html");
    } else {
        $("#payment_message").text(res["message"]);
    }
}


function submitPaymentInformation(event) {
    event.preventDefault();

    $.ajax(
        "api/payment", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: payment_form.serialize(),
            success: res => handlePaymentResult(res),
            error: res => handleFailure(res)
        }
    );
}


function handleResultData(resultData){
    let total_message = $("#total_amount_message");
    let total = 0;
    let cartInfo = resultData[0]["shoppingCart"];
    for (let i=0; i<cartInfo.length;i++) {
        for (let obj in cartInfo[i]) {
            total += parseInt(cartInfo[i][obj]["price"]) * parseInt(cartInfo[i][obj]["qty"]);
        }
    }

    let rowHTML = "<h3>Total cart cost is $" + total + "</h3>";
    total_message.append(rowHTML);




}


$.ajax({
    url: "api/index",
    method: "GET",
    data: "JSON",
    success: (resultData) => handleResultData(resultData)
});

payment_form.submit(submitPaymentInformation);