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

function handleSingleStar(resultData) {
    let singleStarTable = jQuery("#single_star_table_body");

    let star = resultData[0];
    let rowHTML = "";
    rowHTML += "<tr>";

    rowHTML += "<td>" + star["name"] + "</td>";
    rowHTML += "<td>" + star["birthYear"] + "</td>";

    let sep = "";
    rowHTML += "<td>";
    for (let j = 0; j < star["movies"].length; j++) {
        rowHTML +=
            sep + "<a href=\"single-movie.html?id=" + star["movies"][j]["id"] + "\">" +
            star["movies"][j]["title"] + "</a>";
        sep = ", ";
    }
    rowHTML += "</td>";
    rowHTML += "</tr>";
    singleStarTable.append(rowHTML);
}

let starId = getParameterByName('id');

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/single-star?id=" + starId,
    success: (resultData) => handleSingleStar(resultData)
});