let TABLE = "<table border=1px>\n" +
    "<thead>\n" +
    "<tr>\n" +
    "<th>Title</th>\n" +
    "<th>Quantity</th>\n" +
    "<th>Add</th>\n" +
    "<th>Subtract</th>\n" +
    "<th>Remove</th>\n" +
    "<th>Price</th>\n" +
    "</tr>\n"+
    "</thead>\n"+
    "<tbody>\n";

function handleClick(id, name) {
    let params = "id=" + id + "&action=" + name;

    console.log(params);

    $.ajax({
        url: "api/index?" + params,
        method: "POST",
        success: location.reload()
    })
}

function handleResult(data) {
    let display = $("#shopping-cart-display");
    let cartInfo = data["shoppingCart"];

    if (cartInfo === undefined || cartInfo.length === 0) {
        display.append("Your shopping cart is empty.");
        return;
    }

    let rowHTML = TABLE;
    let total = 0;

    for (let i=0; i<cartInfo.length;i++) {
        rowHTML += "<tr>";
        rowHTML += "<td>" + cartInfo[i]["title"] + "</td>";
        rowHTML += "<td>" + cartInfo[i]["qty"] + "</td>";
        rowHTML += "<td><button name=\"add\" id=\"" + cartInfo[i]["id"] +"\" onClick=\"handleClick(this.id, this.name)\">Add</button></td>";
        rowHTML += "<td><button name=\"subtract\" id=\"" + cartInfo[i]["id"] +"\" onClick=\"handleClick(this.id, this.name)\">Subtract</button></td>";
        rowHTML += "<td><button name=\"remove\" id=\"" + cartInfo[i]["id"] +"\" onClick=\"handleClick(this.id, this.name)\">Remove</button></td>";
        let price = parseInt(cartInfo[i]["qty"]) * 10;
        total += price;
        rowHTML += "<td>$" + price + "</td>";
        rowHTML += "</tr>";
    }

    rowHTML += "</tbody>" +
        "<tfoot>" +
        "<tr><td style=\"text-align:right\" colspan=5>Total Price</td>" +
        "<td>$" + total + "</td></tr>"+
        "</tfoot></table>";

    rowHTML += "<br>";
    rowHTML += "<a href=\"checkout.html\">" + "Proceed to checkout" + "</a>";

    display.append(rowHTML);
}


$.ajax({
    dataType: "json",
    method: "GET",
    url: "api/index",
    success: handleResult
});
