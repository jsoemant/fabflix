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

function handleClick(event) {
    console.log(event);

    let PARAMETERS = {
        "addId": event["target"]["id"],
        "addQty": 1,
        "addPrice": 10,
        "addTitle": event["target"]["name"]
    }

    $.ajax(
        "api/index", {
            method: "POST",
            data: $.param(PARAMETERS)
        }
    );
}
function handleGenreChange(id) {
    const PARAMETERS = [
        {'name': 'title', "value": ""},
        {'name':'year', "value": ""},
        {'name':'genre', "value": id},
        {'name':'director', "value": ""},
        {'name':'star', "value": ""},
        {'name':'offset', "value": "0"},
        {'name':'limit', "value": "10"},
        {'name':'order', "value": "TITLEASCRATINGASC"}
    ];


    $.ajax(
        "api/index", {
            method: "POST",
            data: $.param(PARAMETERS),
            success: () => {
                window.location.replace("movie-list.html");
            }
        }
    );
}


function handleSingleMovie(resultData) {
    let movies = resultData[0];

    let singleMovieTable = jQuery("#single_movie_table_body");
    let rowHTML = "";
    rowHTML += "<tr>";
    rowHTML += "<td>" + movies["title"] + "</td>";
    rowHTML += "<td>" + movies["year"] + "</td>";
    rowHTML += "<td>" + movies["director"] + "</td>";

    rowHTML += "<td>";
    let sep = "";
    for (let j = 0; j < movies["genres"].length; j++) {
        rowHTML +=
            sep + "<a " +
            "id=" + movies["genres"][j]["id"] +
            " href=\"#\" " +
            "onClick=handleGenreChange(this.id)" +
            "\>" +
            movies["genres"][j]["name"] + "</a>";
        sep = ", ";
    }
    rowHTML += "</td>";


    rowHTML += "<td>";
    sep = "";
    for (let j = 0; j < movies["stars"].length; j++) {
        rowHTML +=
            sep + "<a href=\"single-star.html?id=" + movies["stars"][j]["id"] + "\">" +
            movies["stars"][j]["name"] + "</a>";
        sep = ", ";
    }
    rowHTML += "</td>";

    rowHTML += "<td>" + (movies["rating"] ? movies["rating"] : "N/A") + "</td>";

    rowHTML += "<td>" +
        "<button id=" +
        movies["id"] +
        " name=\"" +
        movies["title"] +
        "\" onClick=handleClick(event)>+</button>" +
        "</td>";

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