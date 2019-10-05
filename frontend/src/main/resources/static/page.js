$("#search-button").on("click", function () {
    var value = $("#text").val();
    console.log("Value: " + value);
    $.ajax({
            url: "/search",
            type: "get",
            contentType: "application/json",
            data:
                {
                    path: value
                },
            success: function (data) {
                console.log(data);
                $("#list1").innerHTML += "";
                var iHtml = "";
                data.callerLst.forEach(function (data) {
                    iHtml += "<a href=\"#\" class=\"list-group-item list-group-item-action\">" + data + "</a>";
                })
                $("#list1").html(iHtml);

            },
            error: function (e) {
                console.log(e);
            }
        }
    )
});