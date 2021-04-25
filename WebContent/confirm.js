let message = $("#total_message");
let table = $("#order_table_body");


function displayOrder(res){

    let rowHTML = "";
    let total=0;

    for (let i=0; i<res.length; i++) {
        rowHTML += "<tr>";

        rowHTML += "<td>"+ res[i]["title"]+"</td>";
        rowHTML += "<td>"+ res[i]["saleId"] + "</td>";
        rowHTML += "<td>1</td>";
        rowHTML += "<td>$10</td>";
        total+=10;
        rowHTML += "</tr>";
    }

    table.append(rowHTML);
    message.append("Your total is $" + total);

    $.ajax({
        url: "api/index",
        method: "DELETE"
    })
}


$.ajax({
    url: "api/order",
    method: "POST",
    success: (res) => {
        displayOrder(res)
    }
})