let limit_form = $("#limit_form");
let order_form = $("#order_form");

function submitForm(formSubmitEvent) {
    formSubmitEvent.preventDefault();

    let type = formSubmitEvent.currentTarget[0].name;

    if (type === "limit") {
        $.ajax(
            "api/index", {
                method: "POST",
                data: limit_form.serialize() + "&offset=0",
                success: () => {
                    window.location.replace("movie-list.html");
                }
            }
        );
    } else if (type === "order") {
        $.ajax(
            "api/index", {
                method: "POST",
                data: order_form.serialize() + "&offset=0",
                success: () => {
                    window.location.replace("movie-list.html");
                }
            }
        );
    }
}

limit_form.submit(submitForm);
order_form.submit(submitForm);