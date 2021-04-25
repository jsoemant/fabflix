function handleOnClick(id) {

    $.ajax({
        dataType: "json",  // Setting return data type
        method: "GET",// Setting request method
        url: "api/index", // Setting request url
        success: (resultData) => {
            let limit = parseInt(resultData[0]["searchParameters"][5]["limit"]);
            let newOffset = parseInt(resultData[0]["searchParameters"][1]["offset"]);


            if (id === "prev") {
                if (newOffset - limit < 0) {
                    newOffset = 0;
                } else {
                    newOffset -= limit;
                }
            } else {
                newOffset += limit;
            }

            $.ajax(
                "api/index", {
                    method: "POST",
                    data: "offset=" +newOffset,
                    success: () => {
                        window.location.replace("movie-list.html");
                    }
                }
            );
        }
    });
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

function handleAddToCart(event) {
    const PARAMETERS = {
        "addId": event["target"]["id"],
        "addTitle": event["target"]["name"],
        "addQty": 1,
        "addPrice": 10
    }

    $.ajax(
        "api/index", {
            method: "POST",
            data: $.param(PARAMETERS)
        }
    );
}

function displayResult(resultData) {


    let movieListTableBodyElement = jQuery("#movie_list_table_body");

    let movies = resultData["data"];

    for (let i = 0; i < movies.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<td>" +
            "<a href=\"single-movie.html?id=" + movies[i]["movie_id"] + "\">" +
            movies[i]["movie_title"] + "</a>" +
            "</td>";
        rowHTML += "<td>" + movies[i]["movie_year"] + "</td>";
        rowHTML += "<td>" + movies[i]["movie_director"] + "</td>";

        rowHTML += "<td>";
        let sep = "";
        for (let j = 0; j < movies[i]["genres"].length; j++) {
            rowHTML +=
                sep + "<a " +
                "id=" + movies[i]["genres"][j]["id"] +
                " href=\"#\" " +
                "onClick=handleGenreChange(this.id)" +
                "\>" +
                movies[i]["genres"][j]["name"] + "</a>";
            sep = ", ";
        }
        rowHTML += "</td>";

        rowHTML += "<td>";
        sep = "";
        for (let j = 0; j < movies[i]["stars"].length; j++) {
            rowHTML +=
                sep + "<a href=\"single-star.html?id=" + movies[i]["stars"][j]["id"] + "\">" +
                movies[i]["stars"][j]["name"] + "</a>";
            sep = ", ";
        }
        rowHTML += "</td>";

        rowHTML += "<td>" + (movies[i]["movie_rating"] ? movies[i]["movie_rating"] : "N/A") + "</td>";

        rowHTML += "<td>";
        rowHTML += "<button id=" +
            movies[i]["movie_id"] +
            " name=\"" + movies[i]["movie_title"] + "\"" +
            " onClick=handleAddToCart(event)>+" +
            "</button>"
        rowHTML += "</td>";

        rowHTML += "</tr>";
        movieListTableBodyElement.append(rowHTML);
    }


    rowHTML = "";

    if (resultData["index"] > 0) {
        rowHTML += "<button id=\"prev\" onClick=handleOnClick(this.id) >Prev</button>";
    }
    if ((resultData["index"] + resultData["limit"]) < resultData["max"]) {
        rowHTML += "<button id=\"next\" onClick=handleOnClick(this.id)> Next</button>";
    }

    $("#page_button").append(rowHTML);

    let message = "Displaying " + resultData["index"] + "-" + (resultData["index"] + movies.length) + " of " +
        resultData["max"];

    $("#page_count_message").append(message);


}

function handleResult(resultData) {

    let result = resultData[0]["searchParameters"];
    let parametersArray = [];
    for (let i = 0; i < result.length; i++) {
        parametersArray.push($.param(result[i]));
    }
    let parameters = parametersArray.join("&");

    $.ajax({
        dataType: "json",
        method: "GET",
        url: "api/movie-list?" + parameters,
        success: (resultData) => displayResult(resultData)
    })
    $("#limit_form_message").text("Displaying max of " + result[5]["limit"]);
    $("#order_form_message").text("Currently ordering by " + result[7]["order"]);
}


$.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/index", // Setting request url
    success: (resultData) => handleResult(resultData)// Setting callback function to handle data returned successfully by the SingleStarServlet
});

