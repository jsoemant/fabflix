let title_form = $("#title-form");
let genre_form = $("#genre-form");
let search_form = $("#search-form");
let full_form = $("#full-form");

function handleOnChange(id, value) {
    let params;
    if (id === "genre-form") {
        params = "genre=" + value;
    } else {
        params = "title=" + value;
    }
    params += "&limit=10&offset=0&order=TITLEASCRATINGASC";
    window.location.replace("movie-list.html?" + params);
}

function handleSubmitAdvanced(e) {
    e.preventDefault();
    let params = search_form.serialize();
    params += "&limit=10&offset=0&order=TITLEASCRATINGASC";
    window.location.replace("movie-list.html?" + params);
}

function handleSubmitFull(e) {
    e.preventDefault();
    let params = full_form.serialize();
    params += "&limit=10&offset=0&order=TITLEASCRATINGASC";
    window.location.replace("movie-list.html?" + params);
}

// Load Genres into Genre Form
function handleGenreResult(res) {
    let html = "<option value=\"\" selected hidden disabled>Select Genre</option>";
    res.forEach(element => {
        let option = "<option value=" + element["id"] + ">" + element["name"] + "</option>";
        html += option;
    });
    genre_form.append(html);
}

$.ajax({
    dataType: "json",
    url: "api/genre",
    method: "GET",
    success: handleGenreResult
})

// Load Titles into Title Form
const TITLES = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
    "A","B","C","D","E","F","G","H","I","J","K","L","M",
    "N","O","P","Q","R","S","T","U","V","W","X","Y","Z", "*"];

let html = "<option value=\"\" selected hidden disabled>Select Title</option>";
TITLES.forEach(element => {
    let option = "<option value=" + element + ">" + element + "</option>";
    html += option;
});
title_form.append(html);

// Bind onSubmit
search_form.submit(handleSubmitAdvanced);
full_form.submit(handleSubmitFull);