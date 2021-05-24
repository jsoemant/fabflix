let TABLE = "<table border=1px>\n" +
    "<thead>\n" +
    "<tr>\n" +
    "<th>Title</th>\n" +
    "<th>Year</th>\n" +
    "<th>Director</th>\n" +
    "<th>Genres</th>\n" +
    "<th>Stars</th>\n" +
    "<th>Rating</th>\n" +
    "<th>Price</th>\n" +
    "<th>Add to Cart</th>\n" +
    "</tr>\n"+
    "</thead>\n"+
    "<tbody>\n";

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

function handleChange(id, value) {
    let search = new URLSearchParams(window.location.search);
    if (id === "limit-form") {
        search.set("limit", value);
        search.set("offset", "0");
    } else {
        search.set("order", value);
        search.set("offset", "0");
    }
    window.location.search = search;
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

function handlePageClick(id) {
    let search = new URLSearchParams(window.location.search);
    let limit = search.get("limit");
    let offset = search.get("offset");
    let total;
    if (id === "next") {
        total = parseInt(offset) + parseInt(limit);
        search.set("offset", total);
    } else {
        total = parseInt(offset) - parseInt(limit);
        search.set("offset", ((total < 0) ? "0" : total.toString()));
    }
    window.location.search = search;
}

function handleMovieResult(resultData) {
    let movie_list = $("#movie-list");
    let movies = resultData["data"];

    // Create movie list table.
    let rowHTML = TABLE;
    for (let i = 0; i < movies.length; i++) {
        rowHTML += "<tr>";

        // Movie title
        let returnURL = "&return_url=" + encodeURIComponent(window.location.href);
        rowHTML += "<td>";
        rowHTML +=
            "<a href=\"single-movie.html?id=" + movies[i]["movie_id"] + returnURL + "\">" +
            movies[i]["movie_title"] + "</a>";
        rowHTML += "</td>";

        rowHTML += "<td>" + movies[i]["movie_year"] + "</td>";
        rowHTML += "<td>" + movies[i]["movie_director"] + "</td>";

        // Movie genres
        let params = "&limit=10&offset=0&order=TITLEASCRATINGASC";
        rowHTML += "<td>";
        let sep = "";
        if (movies[i]["genres"].length === 0) {
            rowHTML += "N/A";
        }
        else {
            for (let j = 0; j < movies[i]["genres"].length; j++) {
                rowHTML +=
                    sep + "<a href=\"movie-list.html?genre=" + movies[i]["genres"][j]["id"] + params + "\">" +
                    movies[i]["genres"][j]["name"] + "</a>";
                sep = ", ";
            }
        }
        rowHTML += "</td>";

        // Movie stars
        rowHTML += "<td>";
        sep = "";

        if (movies[i]["stars"].length === 0) {
            rowHTML += "N/A";
        } else {
            for (let j = 0; j < movies[i]["stars"].length; j++) {
                rowHTML +=
                    sep + "<a href=\"single-star.html?id=" + movies[i]["stars"][j]["id"] + returnURL + "\">" +
                    movies[i]["stars"][j]["name"] + "</a>";
                sep = ", ";
            }
        }

        rowHTML += "</td>";

        rowHTML += "<td>" + (movies[i]["movie_rating"] ? movies[i]["movie_rating"] : "N/A") + "</td>";
        rowHTML += "<td>$10</td>";

        // Movie add to cart buttons
        rowHTML += "<td>";
        rowHTML += "<button id=\"" + movies[i]["movie_id"] + "\"" +
            " name=\"" + movies[i]["movie_title"] + "\"" +
            " onClick=\"handleAddToCart(this.id, this.name)\">Add to Cart</button>";
        rowHTML += "</td>";

        rowHTML += "</tr>";
    }
    movie_list.append(rowHTML);

    // Add Next and Prev buttons.
    rowHTML = "";
    if (resultData["index"] > 0) {
        rowHTML += "<button id=\"prev\" onClick=\"handlePageClick(this.id)\">Prev</button>";
    }
    if ((resultData["index"] + resultData["limit"]) < resultData["max"]) {
        rowHTML += "<button id=\"next\" onClick=\"handlePageClick(this.id)\">Next</button>";
    }
    $("#page-button").append(rowHTML);

    // Show the current amount of results being displayed.
    rowHTML = resultData["index"] + "-" + (resultData["index"] + movies.length) + " of " + resultData["max"];
    $("#page-message").append(rowHTML);
}


// Add currently selected limit and order to page display
let limit = getParameterByName("limit");
if (limit !== null ) {
    $("#limit-message").append(" = " + limit);
}

let order = getParameterByName("order");
if (order !== null) {
    $("#order-message").append(" = " + order);
}

// Request movie list data to display
$.ajax({
    dataType: "json",
    method: "GET",
    url: "api/movie-list" + window.location.search,
    success: handleMovieResult
})
