let search_form = $("#search_form");

function submitSearchForm(formSubmitEvent) {
    formSubmitEvent.preventDefault();
    const params = search_form.serialize();

    if (params === "title=&year=&director=&star=") {
        const message = "Nothing was entered to search";
        $("#search_error_message").text(message);
    } else {
        $.ajax(
            "api/index", {
                method: "POST",
                data: search_form.serialize(),
                success: () => {
                    window.location.replace("movie-list.html");
                }
            }
        );
    }
}

search_form.submit(submitSearchForm);

