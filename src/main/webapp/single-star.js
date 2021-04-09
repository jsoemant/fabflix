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
    let singleStarInfoElement = jQuery("#single_star_info");
    singleStarInfoElement.append(resultData[0]["name"]);

    let singleStarTable = jQuery("#single_star_table_body");
    let rowHTML = "";
    rowHTML += "<tr>";
    rowHTML += "<td>" + resultData[0]["birthYear"] + "</td>";
    let sep = "";
    rowHTML += "<td>";
    for (let j = 0; j < resultData[0]["movies"].length; j++) {
        rowHTML +=
            sep +
            "<a href=\"single-movie.html?id=" + resultData[0]["moviesId"][j] + "\">" +
            resultData[0]["movies"][j] + "</a>";
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