function handleDelete(id) {

    $.ajax({
        url: "api/index?addId=" + id,
        method: "DELETE",
        success: () => {
            window.location.replace("cart.html");
        }
    });
}

function handleClick(event) {
    let PARAMETERS = {};

    if (event["target"]["name"] === "plus") {
         PARAMETERS = {
            "addId": event["target"]["id"],
            "addQty": 1,
        }
    } else {
         PARAMETERS = {
            "addId": event["target"]["id"],
            "addQty": -1,
        }
    }

    $.ajax(
        "api/index", {
            method: "POST",
            data: $.param(PARAMETERS),
            success: () => {
                window.location.replace("cart.html");
            }
        }
    );
}


function handleResult(data) {
    let table = $("#shopping-cart-display");
    let cartInfo = data[0]["shoppingCart"];

    if (cartInfo.length === 0) {
        table.append("No items in cart, cannot proceed to payment!");
        return;
    }

    let rowHTML = ("    <table border=1px>\n" +
        "        <thead>\n" +
        "        <tr>\n" +
        "            <th>Title</th>\n" +
        "            <th>Quantity</th>\n" +
        "            <th>Price Per Movie</th>\n" +
        "            <th>Increase</th>\n" +
        "            <th>Decrease</th>\n" +
        "            <th>Delete</th>\n" +
        "        </tr>\n"+
        " </thead>\n"+
        " <tbody>\n");


    for (let i=0; i<cartInfo.length;i++) {
        for (let obj in cartInfo[i]) {
            rowHTML += "<tr>";
            rowHTML += "<td>" + cartInfo[i][obj]["title"] + "</td>";
            rowHTML += "<td>" + cartInfo[i][obj]["qty"] + "</td>";
            rowHTML += "<td>$" + cartInfo[i][obj]["price"] + "</td>";
            rowHTML += "<td><button name=plus id=" + obj   +" onClick=handleClick(event)>+</button></td>";
            rowHTML += "<td><button name=minus id=" + obj  +" onClick=handleClick(event)>-</button></td>";
            rowHTML += "<td><button id=" + obj + " onClick=handleDelete(this.id)>Delete</button></td>";
            rowHTML += "</tr>";
        }
    }

    rowHTML += "        </tbody>\n" +
        "    </table>";

    rowHTML += "<a href=\"payment.html\" >Proceed to payment</a>";

    table.append(rowHTML);
}


$.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/index", // Setting request url
    success: (resultData) => handleResult(resultData)// Setting callback function to handle data returned successfully by the SingleStarServlet
});
