
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

function addGenre(res) {
    let row ="";
    for(let i=0; i<res.length; i++) {
        row += "<option value=" + res[i]["id"] + " >" + res[i]["name"] + "</option>";
    }
    $("#genre").append(row);
}

$.ajax({
    url: "api/genre",
    method: "GET",
    success: (res) => {
        addGenre(res);
    }
})


let rowHTML ="";
rowHTML = "";
for (let i = 0; i<TITLES.length;i++) {
    rowHTML += "<option value=" + TITLES[i] + ">" +
        TITLES[i] + "</option>";
}
$("#title").append(rowHTML);