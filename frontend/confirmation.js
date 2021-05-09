let message = $("#total-message");
let table = $("#order-table-body");

function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function displayOrder(res){
    let rowHTML = "";
    let total = 0;
    for (let i=0; i<res.length; i++) {
        rowHTML += "<tr>";
        rowHTML += "<td>"+ res[i]["title"]+ "</td>";
        rowHTML += "<td>" + res[i]["saleId"]+"</td>";
        rowHTML += "<td>" +res[i]["qty"]+"</td>";

        let price = parseInt(res[i]["qty"]) * 10;
        total += price

        rowHTML += "<td>$" + price +"</td>";
        rowHTML += "</tr>";
    }

    table.append(rowHTML);
    message.text("Your shopping cart total was $" + total);
}

let orderId = getParameterByName("orderId");

$.ajax({
    url: "api/order?orderId=" + orderId,
    method: "GET",
    success: displayOrder
})