var chartConfig = {
    chart: {
        container: "#tree-graph",
        rootOrientation: "EAST",
        connectors: {
            type: "step",
            style: {
                "stroke-width": 2,
                "stroke": "#ccc",
                'arrow-end': 'block-wide-long'
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
            onCreateNode: function (treeNode, treeNodeDom) {
                $(treeNodeDom).prepend('<button onclick="copyToInput(\'' + treeNode.text.dataFullPath + '\')">Copy to input box.</button>');
            }
        }
    },
    nodeStructure: {}
};

var calleeChartConfig = {
    chart: {
        container: "#callee-tree-graph",
        rootOrientation: "WEST",
        connectors: {
            type: "step",
            style: {
                "stroke-width": 2,
                "stroke": "#ccc",
                'arrow-end': 'block-wide-long'
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
            onCreateNode: function (treeNode, treeNodeDom) {
                $(treeNodeDom).prepend('<button onclick="copyToInput(\'' + treeNode.text.dataFullPath + '\')">Copy to input box.</button>');
            }
        }
    },
    nodeStructure: {},
    isInitialized: true
};


function copyToInput(value) {
    $("#text").val(value);
}

$("#search-button").on("click", function () {
    document.getElementById("loader").style.display = "block";
    document.getElementById("overlay").style.display = "block";
    $("#pills-caller-tab").click();
    var value = $("#text").val().trim();
    console.log("Value: " + value);
    if (value == '') {
        $("#tree-graph").html("<b>Please enter your keyword.</b>");
        document.getElementById("loader").style.display = "none";
        document.getElementById("overlay").style.display = "none";
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
                //$("#calleeLst").html(data.calleeLst[0].replace(/\n/g, "</br>") + "<br/>" + data.calleeLst[1]);
                calleeChartConfig.nodeStructure = data.calleeNodeStructure;
                calleeChartConfig.isInitialized = false;
                $("#callee-tree-graph").html("<b>Refreshing...</b>");
                document.getElementById("loader").style.display = "none";
                document.getElementById("overlay").style.display = "none";
                $("#callee-list").html('');
                data.calleeLst.forEach(function(value){
                    $("#callee-list").append(value + "<br/>");
                })
            },
            error: function (e) {
                console.log(e);
                $("#tree-graph").html("<b>ERROR: " + e.responseJSON.error + ". " + e.responseJSON.message + "</b>");
                $("#callee-tree-graph").html("<b>ERROR: " + e.responseJSON.error + ". " + e.responseJSON.message + "</b>");
                document.getElementById("loader").style.display = "none";
                document.getElementById("overlay").style.display = "none";
            }
        }
    )
});

$("#pills-callee-tab").on("click", function () {
    setTimeout(function () {
        if (calleeChartConfig.isInitialized === false) {
            new Treant(calleeChartConfig);
            calleeChartConfig.isInitialized = true;
        }
    }, 1000);

});

