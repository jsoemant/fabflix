const GENRES = [
    {id: 1, name: "Action"},
    {id: 2, name: "Adult"},
    {id: 3, name: "Adventure"},
    {id: 4, name: "Animation"},
    {id: 5, name: "Biography"},
    {id: 6, name: "Comedy"},
    {id: 7, name: "Crime"},
    {id: 8, name: "Documentary"},
    {id: 9, name: "Drama"},
    {id: 10, name: "Family"},
    {id: 11, name: "Fantasy"},
    {id: 12, name: "History"},
    {id: 13, name: "Horror"},
    {id: 14, name: "Music"},
    {id: 15, name: "Musical"},
    {id: 16, name: "Mystery"},
    {id: 17, name: "Reality-TV"},
    {id: 18, name: "Romance"},
    {id: 19, name: "Sci-Fi"},
    {id: 20, name: "Sport"},
    {id: 21, name: "Thriller"},
    {id: 22, name: "War"},
    {id: 23, name: "Western"},
];

const TITLES = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
    "A","B","C","D","E","F","G","H","I","J","K","L","M",
    "N","O","P","Q","R","S","T","U","V","W","X","Y","Z", "*"];

let title_form = $("#title_form");
let genre_form = $("#genre_form");


function submitForm(formSubmitEvent) {
    formSubmitEvent.preventDefault();

    let type = formSubmitEvent.currentTarget[0].name;
    if (type === "title") {
        $.ajax(
            "api/index", {
                method: "POST",
                data: title_form.serialize(),
                success: () => {
                    window.location.replace("movie-list.html");
                }
            }
        );
    } else {
        $.ajax(
            "api/index", {
                method: "POST",
                data: genre_form.serialize(),
                success: () => {
                    window.location.replace("movie-list.html");
                }
            }
        );
    }
}

title_form.submit(submitForm);
genre_form.submit(submitForm);

let rowHTML ="";
for (let i = 0; i<GENRES.length;i++) {
    rowHTML += "<option value=" + GENRES[i].id + ">" +
        GENRES[i].name + "</option>";
}
$("#genre").append(rowHTML);
rowHTML = "";
for (let i = 0; i<TITLES.length;i++) {
    rowHTML += "<option value=" + TITLES[i] + ">" +
        TITLES[i] + "</option>";
}
$("#title").append(rowHTML);