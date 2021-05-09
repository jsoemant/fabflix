function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Uses regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function handleAddResponse(response) {
    if (response["status"] === "success") {
        window.alert("Added to cart!");
    } else {
        window.alert("Unable to add to cart!");
    }
}

function handleAddToCart(id, name) {
    let params = "id=" + id + "&title=" + name + "&action=add";

    $.ajax({
        url: "api/index?" + params,
        method: "POST",
        success: handleAddResponse
    })
}

function handleSingleMovieResult(res) {
    let singleMovieTable = $("#single-movie-table-body");
    let rowHTML = "";
    rowHTML += "<tr>";
    rowHTML += "<td>" + res["title"] + "</td>";
    rowHTML += "<td>" + res["year"] + "</td>";
    rowHTML += "<td>" + res["director"] + "</td>";

    // Single movie genres
    rowHTML += "<td>";
    let sep = "";
    let params = "&limit=10&offset=0&order=TITLEASCRATINGASC";

    for (let j = 0; j < res["genres"].length; j++) {
        rowHTML +=
            sep + "<a " +
            "href=\"movie-list.html?genre=" + res["genres"][j]["id"] + params + "\" " +
            "\>" +
            res["genres"][j]["name"] + "</a>";
        sep = ", ";
    }
    rowHTML += "</td>";

    let returnParam = "";
    if (returnURL !== null) {
        returnParam = "&return_url=" + encodeURIComponent(returnURL);
    }

    // Single movie stars
    rowHTML += "<td>";
    sep = "";

    if (res["stars"].length === 0){
        rowHTML += "N/A";
    }
    else {
        for (let j = 0; j < res["stars"].length; j++) {
            rowHTML +=
                sep + "<a href=\"single-star.html?id=" + res["stars"][j]["id"] + returnParam + "\">" +
                res["stars"][j]["name"] + "</a>";
            sep = ", ";
        }
    }
    rowHTML += "</td>";

    rowHTML += "<td>" + (res["rating"] ? res["rating"] : "N/A") + "</td>";
    rowHTML += "<td>$10</td>";

    // Single movie buttons
    rowHTML += "<td>" +
        "<button id=\"" + res["id"] + "\" name=\"" + res["title"] +
        "\" onClick=\"handleAddToCart(this.id, this.name)\">+</button>" +
        "</td>";

    rowHTML += "</tr>";

    singleMovieTable.append(rowHTML);
}

// jQuery to load single-movie page data
let movieId = getParameterByName('id');
let returnURL = getParameterByName('return_url');

if (returnURL !== null) {
    let tmpLink = "<a href=\"" + returnURL + "\">" +
    "Return To Movie List</a>";
    $("#link").append(tmpLink);
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/single-movie?id=" + movieId,
    success: handleSingleMovieResult
});