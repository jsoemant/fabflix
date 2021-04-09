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

function handleSingleMovie(resultData) {
    let singleMovieInfoElement = jQuery("#single_movie_info");
    singleMovieInfoElement.append(resultData[0]["title"]);

    let singleMovieTable = jQuery("#single_movie_table_body");
    let rowHTML = "";
    rowHTML += "<tr>";
    rowHTML += "<td>" + resultData[0]["year"] + "</td>";
    rowHTML += "<td>" + resultData[0]["director"] + "</td>";
    rowHTML += "<td>" + resultData[0]["genres"] + "</td>";
    rowHTML += "<td>";
    let sep = "";
    for (let j = 0; j < resultData[0]["stars"].length; j++) {
        rowHTML +=
            sep +
            "<a href=\"single-star.html?id=" + resultData[0]["stars_id"][j] + "\">" +
            resultData[0]["stars"][j] + "</a>";
        sep = ", ";
    }
    rowHTML += "</td>";
    rowHTML += "<td>" + resultData[0]["rating"] + "</td>";
    rowHTML += "</tr>";
    singleMovieTable.append(rowHTML);
}

let movieId = getParameterByName('id');

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/single-movie?id=" + movieId,
    success: (resultData) => handleSingleMovie(resultData)
});