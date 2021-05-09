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

function handleSingleStar(res) {
    let singleStarTable = $("#single-star-table-body");

    let rowHTML = "<tr>";

    rowHTML += "<td>" + res["name"] + "</td>";
    rowHTML += "<td>" + res["birthYear"] + "</td>";

    // Single movies
    rowHTML += "<td>";
    let returnParam = "";
    if (returnURL !== null) {
        returnParam = "&return_url=" + encodeURIComponent(returnURL);
    }
    let sep = "";

    if (res["movies"].length === 0 ) {
        rowHTML += "N/A";
    } else {
        for (let j = 0; j < res["movies"].length; j++) {
            rowHTML +=
                sep + "<a href=\"single-movie.html?id=" + res["movies"][j]["id"] + returnParam + "\">" +
                res["movies"][j]["title"] + "</a>";
            sep = ", ";
        }
    }
    rowHTML += "</td>";

    rowHTML += "</tr>";
    singleStarTable.append(rowHTML);
}

// jQuery to load single-star page data
let starId = getParameterByName('id');
let returnURL = getParameterByName('return_url');

if (returnURL !== null) {
    let tmpLink = "<a href=\"" + returnURL + "\">" +
        "Return To Movie List</a>";
    $("#link").append(tmpLink);
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/single-star?id=" + starId,
    success: handleSingleStar
});