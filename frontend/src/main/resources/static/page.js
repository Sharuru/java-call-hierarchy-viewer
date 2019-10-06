var chartConfig = {
    chart: {
        container: "#tree-graph",
        rootOrientation: "EAST",
        connectors: {
            type: "step",
            style: {
                "stroke-width": 2,
                "stroke": "#ccc"
            }
        },
        node: {
            collapsable: false,
            HTMLclass: 'nodeStyle'
        },
        levelSeparation: 60,
        siblingSeparation: 60,
        subTeeSeparation: 60
    },
    nodeStructure: {}
};

$("#search-button").on("click", function () {
    $("#pills-caller-tab").click();
    var value = $("#text").val().trim();
    console.log("Value: " + value);
    $("#tree-graph").html("Searching...");
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
                chartConfig.nodeStructure = data.nodeStructure;
                new Treant(chartConfig);
                $("#calleeLst").html(data.calleeLst[0].replace(/\n/g, "</br>") + "<br/>" + data.calleeLst[1]);

            },
            error: function (e) {
                console.log(e);
                $("#tree-graph").html("<b>Error: " + e.responseJSON.error + ", " + e.responseJSON.message + "</b><br/>" + e.responseJSON.trace);
            }
        }
    )
});

