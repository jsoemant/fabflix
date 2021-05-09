let payment_form = $("#payment-form");


function handleFailure(res) {
    $("#payment-message").text(res["responseJSON"]["message"]);
}

function handlePaymentResult(res) {
    if (res["status"] === "success") {
        window.location.replace("confirmation.html?orderId=" + res["orderId"]);
    } else {
        $("#payment-message").text(res["message"]);
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
    let total_message = $("#total-message");
    let total = 0;
    let cartInfo = resultData["shoppingCart"];
    for (let i=0; i<cartInfo.length;i++) {
        total += 10 * parseInt(cartInfo[i]["qty"]);
    }

    let rowHTML = "<h3>Total cart cost: $" + total + "</h3>";
    total_message.append(rowHTML);
}

$.ajax({
    url: "api/index",
    method: "GET",
    data: "JSON",
    success: handleResultData
});

payment_form.submit(submitPaymentInformation);