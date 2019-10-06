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
        siblingSeparation: 30,
        subTeeSeparation: 30,
        callback: {
            onCreateNode: function(treeNode, treeNodeDom) {
                $(treeNodeDom).prepend('<button onclick="copyToInput(\'' + treeNode.text.dataFullPath + '\')">Copy to input box.</button>');
            }
        }
    },
    nodeStructure: {}
};

function copyToInput(value){
    $("#text").val(value);
}

$("#search-button").on("click", function () {
    $("#pills-caller-tab").click();
    var value = $("#text").val().trim();
    console.log("Value: " + value);
    if(value == ''){
        $("#tree-graph").html("<b>Please enter your keyword.</b>");
        return;
    }
    $("#tree-graph").html("<b>Searching...</b>");
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
                $("#tree-graph").html("<b>ERROR: " + e.responseJSON.error + ". " + e.responseJSON.message + "</b>");
            }
        }
    )
});

