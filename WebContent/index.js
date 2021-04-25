const PARAMETERS = [
    {'name': 'title', "value": ""},
    {'name':'year', "value": ""},
    {'name':'genre', "value": ""},
    {'name':'director', "value": ""},
    {'name':'star', "value": ""},
    {'name':'offset', "value": "0"},
    {'name':'limit', "value": "10"},
    {'name':'order', "value": "TITLEASCRATINGASC"}
];



$.ajax("api/index", {
    method: "POST",
    data: $.param(PARAMETERS)
});


