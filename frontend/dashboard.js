let star_form = $("#star-form");
let movie_form = $("#movie-form");

function handleMetaData(res) {
    let body = $("#metadata-table-body");
    let rowHTML = "";

    for (let i=0; i<res.length; i++){
        rowHTML += "<tr>";
        rowHTML += "<td>" +res[i]["table_name"] + "</td>";
        rowHTML += "<td>";
        let sep = "";

        let columns = res[i]["columns"];

        for (let j=0; j<columns.length; j++) {
            rowHTML += sep + columns[j]["column_name"] + " (" + columns[j]["column_type"] + ")";
            sep = ", ";
        }
        rowHTML += "</td>";
        rowHTML += "</tr>";
    }

    body.append(rowHTML);
}


function handleStarSubmit(event) {
    event.preventDefault();
    console.log(star_form.serialize());

    $.ajax({
        dataType: "json",
        url: "_addstar?" + star_form.serialize(),
        method: "POST",
        success: (res) => {
            window.alert("added star id = " + res["id"]);
        }
    })
}

function handleMovieSubmit(event) {
    event.preventDefault();
    console.log(movie_form.serialize());

    $.ajax({
        dataType: "json",
        url: "_addmovie?" + movie_form.serialize(),
        method: "POST",
        success: (res) => {
            window.alert(res["message"]);
        }
    })
}

star_form.submit(handleStarSubmit);
movie_form.submit(handleMovieSubmit);



$.ajax({
    url: "_metadata",
    dataType: "json",
    method: "GET",
    success: handleMetaData
})