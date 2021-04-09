function handleMovieListPage(resultData) {
    let movieListTableBodyElement = jQuery("#movie_list_table_body");

    for (let i = 0; i < 20; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<td>" +
            "<a href=\"single-movie.html?id=" + resultData[i]['id'] + "\">" +
            resultData[i]["title"] + "</a>" +
            "</td>";
        rowHTML += "<td>" + resultData[i]["year"] + "</td>";
        rowHTML += "<td>" + resultData[i]["director"] + "</td>";
        rowHTML += "<td>" + resultData[i]["genres"] + "</td>";
        rowHTML += "<td>";
        let sep = "";
        for (let j = 0; j < resultData[i]["stars"].length; j++) {
            rowHTML +=
                sep + "<a href=\"single-star.html?id=" + resultData[i]["stars_id"][j] + "\">" +
                resultData[i]["stars"][j] + "</a>";
            sep = ", ";
        }
        rowHTML += "</td>";

        rowHTML += "<td>" + resultData[i]["rating"] + "</td>";
        rowHTML += "</tr>";
        movieListTableBodyElement.append(rowHTML);
    }
}

jQuery.ajax({
   dataType: "json",
   method: "GET",
   url: "api/movie-list",
   success: (resultData) => handleMovieListPage(resultData)
});

