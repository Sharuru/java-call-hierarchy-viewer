$(document).ready(function () {

    // Footer's copyright year
    document.getElementById("copyright-year").innerHTML = new Date().getFullYear();

    /**
     * Inspector panel
     * tooltip registration.
     */
    $('#info-func-btn-1').tooltip({
        'trigger': 'hover',
        'placement': 'bottom',
        'title': 'Set qualified name as keyword'
    });

    /**
     * Inspector panel
     * set current selected node's qualified name as next search keyword.
     */
    $("#info-func-btn-1").on('click', function () {
        $("#keyword-input").val($('#info-qualified-name').text());
    });

    /**
     * Inspector panel
     * draggable registration.
     */
    $("#inspector-panel").draggable({cursor: "move", handle: "div#inspector-panel-title"});

    /**
     * Inspector panel
     * resizeable registration.
     */
    $("#inspector-panel").resizable();


    /**
     * Inspector panel
     * reset panel size registration.
     */
    $("#inspector-panel-reset-handler").on('click', function () {
        $("#inspector-panel").animate({
            left: "4rem",
            top: "14rem",
            width: "30rem",
            height: "10rem"
        }, 500);
    });

    /**
     * Layout reset button
     * reset layout event registration.
     */
    $("#reset-layout-button").on('click', function () {
        if ($("#pills-caller-tab").hasClass('active') === true && typeof globalCallerGraph !== 'undefined' && typeof globalCallerGraph.graph !== 'undefined' ) {
            globalCallerGraph.graph.layout(true);
        } else if($("#pills-callee-tab").hasClass('active') === true && typeof globalCalleeGraph !== 'undefined' && typeof globalCalleeGraph.graph !== 'undefined' ){
            globalCalleeGraph.graph.layout(true);
        }
        $("#inspector-panel").animate({
            left: "4rem",
            top: "14rem",
            width: "30rem",
            height: "10rem"
        }, 500);
    });

    $("#search-area").draggable({cursor: "move", handle: "div#search-area-title"});

    /**
     * Method call collection model
     * button click registration.
     */
    $("#collection-calls-button").on("click", function () {
        $("#collection-calls-modal").modal("show");
    });

    /**
     * Node search button panel
     * tooltip registration.
     */
    $('#node-search-btn').tooltip({
        'trigger': 'hover',
        'placement': 'bottom'
    });

    var lastNodeId = null;
    var lastKeyword = "";
    var nodeIds = new Array();
    var nodeSearchIdx = 0;

    /**
     * Node method search panel
     * main business registration.
     */
    $("#node-search-div-button").on('click', function () {
        if (lastNodeId != null){
            var graph;
            if (calleeBiz) {
                graph = globalCalleeGraph.graph;
            } else {
                graph = globalCallerGraph.graph;
            }
            var lastNode = graph.findById(lastNodeId);
            fillOriginColor(graph, lastNode);
        }
        lastKeyword = "";
        nodeIds = new Array();
        nodeSearchIdx = 0;
        lastNodeId = null;
        $("#node-search-result").html("");
        if($("#search-area").css("display") == "none"){
            $("#search-area").css("display", "block");
            $("#method-keyword-input")[0].select();
        }else{
            $("#search-area").css("display", "none");
        }
    });

    /**
     * Node method search button
     * main business registration.
     */
    $("#node-search-btn").on('click', function () {
        var methodPathArr;
        var graph;
        if (calleeBiz) {
            methodPathArr = globalCalleeGraph.methodPathArr;
            graph = globalCalleeGraph.graph;
        } else {
            methodPathArr = globalCallerGraph.methodPathArr;
            graph = globalCallerGraph.graph;
        }
        if (lastNodeId != null){
            var lastNode = graph.findById(lastNodeId);
            fillOriginColor(graph, lastNode);
            lastNodeId = null;
        }
        $("#node-search-result").html("");
        var keyword = $("#method-keyword-input").val();
        if (keyword == ""){
            lastKeyword = "";
            nodeIds = new Array();
            nodeSearchIdx = 0;
            $("#node-search-result").html("Please input parameter");
            return false;
        }

        if (keyword == lastKeyword){
            var node = graph.findById(nodeIds[nodeSearchIdx]);
            fillSearchColor(graph, node);
            graph.focusItem(nodeIds[nodeSearchIdx]);

            $("#info-qualified-name").text(node.getModel().methodQualifiedName);
            $("#info-method-path").text(node.getModel().uiMethodPath);
            $("#info-method-comment").text(node.getModel().uiMethodComment);
            $("#info-content").animate({scrollTop: 0}, "fast");

            lastNodeId = nodeIds[nodeSearchIdx];
            nodeSearchIdx = nodeSearchIdx + 1;
            $("#node-search-result").html(nodeSearchIdx + " of " + nodeIds.length + " Result(s)");
            if (nodeSearchIdx >= nodeIds.length){
                nodeSearchIdx = 0;
            }
        } else {
            lastKeyword = keyword;
            nodeIds = new Array();
            nodeSearchIdx = 0;

            if (typeof methodPathArr === "undefined"){
                lastKeyword = "";
                $("#node-search-result").html("Method not found");
                return false;
            }
            for (let i in methodPathArr) {
                if (nodeSearchPathFlag) {
                    if (methodPathArr[i].methodPath.indexOf(keyword) != -1) {
                        nodeIds.push(methodPathArr[i].id);
                    }
                } else {
                    if (methodPathArr[i].methodComment.indexOf(keyword) != -1) {
                        nodeIds.push(methodPathArr[i].id);
                    }
                }
            }
            if (nodeIds.length == 0){
                lastKeyword = "";
                $("#node-search-result").html("Method not found");
                return false;
            }
            var node = graph.findById(nodeIds[nodeSearchIdx]);
            fillSearchColor(graph, node);
            graph.focusItem(nodeIds[nodeSearchIdx]);

            $("#info-qualified-name").text(node.getModel().methodQualifiedName);
            $("#info-method-path").text(node.getModel().uiMethodPath);
            $("#info-method-comment").text(node.getModel().uiMethodComment);
            $("#info-content").animate({scrollTop: 0}, "fast");

            lastNodeId = nodeIds[nodeSearchIdx];
            nodeSearchIdx = nodeSearchIdx + 1;
            $("#node-search-result").html(nodeSearchIdx + " of " + nodeIds.length + " Result(s)");
            if (nodeSearchIdx >= nodeIds.length){
                nodeSearchIdx = 0;
            }
        }
    });

    /**
     * Main content panel
     * initialize business.
     */
    $('a[data-toggle="pill"]').on('shown.bs.tab', function (e) {
        if (lastNodeId != null){
            var graph;
            if (calleeBiz) {
                graph = globalCalleeGraph.graph;
            } else {
                graph = globalCallerGraph.graph;
            }
            var lastNode = graph.findById(lastNodeId);
            fillOriginColor(graph, lastNode);
        }
        lastKeyword = "";
        nodeIds = new Array();
        nodeSearchIdx = 0;
        lastNodeId = null;
        $("#node-search-result").html("");
        if($("#search-area").css("display") == "block"){
            $("#node-search-div-button").removeClass("active");
            $("#search-area").css("display", "none");
        }

        $("#collection-calls-button").prop("disabled", "disabled");
        if (e.target.id === 'pills-caller-tab' && typeof globalCallerGraph !== 'undefined' && typeof globalCallerGraph.coll !== 'undefined') {
            drawCollectionModelContent(globalCallerGraph.coll);
            $("#collection-calls-button").removeAttr("disabled");
        } else if (e.target.id === 'pills-callee-tab' && typeof globalCalleeGraph !== 'undefined' && typeof globalCalleeGraph.coll !== 'undefined') {
            drawCollectionModelContent(globalCalleeGraph.coll);
            $("#collection-calls-button").removeAttr("disabled");
        }
        if (e.target.id === 'pills-caller-tab') {
            calleeBiz = false;
        } else {
            calleeBiz = true;
        }

    });

    /**
     * G6 node registration
     */
    G6.registerNode(
        'method-card',
        {
            drawShape: (cfg, group) => {
                group.addShape('rect', {
                    attrs: {
                        x: 0,
                        y: 0,
                        width: 256,
                        height: 64
                    }
                });
                group.addShape('rect', {
                    attrs: {
                        x: 3,
                        y: 0,
                        width: 256,
                        height: 64,
                        fill: cfg.methodType === 'BASE' ? 'rgba(198, 229, 255, 0.6)' : cfg.methodType === 'LOOP-SRC' ? 'rgba(252, 244, 227, 0.6)' : 'rgba(243, 246, 253, 0.6)',
                        stroke: cfg.methodType === 'BASE' ? 'rgba(91, 143, 249, 0.6)' : cfg.methodType === 'LOOP-SRC' ? 'rgba(251, 166, 74, 0.6)' : 'rgba(208, 217, 249, 0.6)',
                        radius: 2
                    }
                });
                group.addShape('text', {
                    attrs: {
                        text: cfg.methodType,
                        x: 4,
                        y: -6,
                        fontSize: 12,
                        fontWeight: 400,
                        textAlign: 'left',
                        textBaseline: 'middle',
                        fill: 'gray'
                    }
                });
                group.addShape('text', {
                    attrs: {
                        text: cfg.uiMethodContext,
                        x: 12,
                        y: 26,
                        fontSize: 12,
                        fontWeight: 600,
                        textAlign: 'left',
                        textBaseline: 'middle',
                        fill: 'black',
                    }
                });
                group.addShape('text', {
                    attrs: {
                        text: cfg.uiMethodCommentSummary,
                        x: 8,
                        y: 54,
                        fontSize: 12,
                        textAlign: 'left',
                        textBaseline: 'middle',
                        fill: 'black',
                    }
                });
                group.addShape('rect', {
                    attrs: {
                        stroke: '',
                        x: 0,
                        y: 0,
                        width: 256,
                        height: 64,
                        fill: '#fff',
                        opacity: 0,
                        cursor: 'pointer'
                    },
                    className: '_id-hover-layer'
                });
                return group;
            }
        }, 'rect');

    let globalCalleeGraph = {};
    let globalCallerGraph = {};
    let calleeBiz = true;
    let apiLink = null;

    /**
     * Node search button
     * main business registration.
     */
    $("#search-button").on('click', function () {

        lastKeyword = "";
        nodeIds = new Array();
        nodeSearchIdx = 0;
        lastNodeId = null;
        $("#node-search-result").html("");

        $("#collection-calls-button").prop("disabled", "disabled");
        $("#search-button").prop("disabled", "disabled");
        $("#search-button").html('<span class="spinner-border spinner-border-sm" role="status"></span>Searching...');

        if ($("#pills-caller-tab").hasClass('active') === true) {
            apiLink = '/getCallerMethodInfo';
            calleeBiz = false;
        } else {
            apiLink = '/getCalleeMethodInfo';
            calleeBiz = true;
        }

        let inputVal = $("#keyword-input").val().trim();

        $.ajax({
                url: apiLink,
                type: "get",
                contentType: "application/json",
                data:
                    {
                        methodQualifiedName: inputVal
                    },
                success: function (data) {
                    $("#search-button").html('<span class="spinner-border spinner-border-sm" role="status"></span>Rendering...');
                    if (typeof globalCalleeGraph.graph !== 'undefined' && calleeBiz) {
                        globalCalleeGraph.graph.destroy();
                        globalCalleeGraph = {};
                    } else if (typeof globalCallerGraph.graph !== 'undefined' && !calleeBiz) {
                        globalCallerGraph.graph.destroy();
                        globalCallerGraph = {};
                    }
                    const minimap = new G6.Minimap({
                        size: [160, 160],
                        type: 'delegate'
                    });

                    let containerId = calleeBiz ? 'callee-graph-container' : 'caller-graph-container';

                    const drawingGraph = new G6.TreeGraph({
                        container: containerId,
                        width: document.getElementById(containerId).scrollWidth,
                        height: document.getElementById(containerId).scrollHeight || window.innerHeight * 0.7,
                        defaultNode: {
                            type: 'method-card',
                            anchorPoints: [[0, 0.5], [1, 0.5]]
                        },
                        defaultEdge: {
                            type: 'cubic-horizontal'
                        },
                        modes: {
                            default: [{
                                type: 'collapse-expand',
                                onChange: function onChange(item, collapsed) {
                                    data = item.getModel();
                                    data.collapsed = collapsed;
                                    collapseNode(data, collapsed);
                                    return true;
                                }
                            }, 'drag-canvas', 'zoom-canvas']
                        },
                        layout: {
                            type: 'compactBox',
                            direction: calleeBiz ? 'LR' : 'RL',
                            getHeight: function getHeight() {
                                return 16;
                            },
                            getWidth: function getWidth() {
                                return 16;
                            },
                            getVGap: function getVGap() {
                                return 60;
                            },
                            getHGap: function getHGap() {
                                return 160;
                            }
                        },
                        plugins: [minimap]

                    });

                    drawingGraph.data(data.treeGraphData);
                    drawingGraph.render();
                    drawingGraph.on('node:contextmenu', ev => {
                        ev.preventDefault();
                        const classname = ev.target.get('className');
                        const item = ev.item;
                        if (classname === '_id-hover-layer' && item) {
                            $("#info-qualified-name").text(item.getModel().methodQualifiedName);
                            $("#info-method-path").text(item.getModel().uiMethodPath);
                            $("#info-method-comment").text(item.getModel().uiMethodComment);
                            $("#info-content").animate({scrollTop: 0}, "fast");
                        }
                    });

                    drawingGraph.layout(true);

                    var methodPathArr = new Array();
                    for (let i in data.methodPathList) {
                        var obj = data.methodPathList[i].split("|@|");
                        var methodPathObj = {};
                        methodPathObj.id = obj[0]
                        methodPathObj.methodPath = obj[1];
                        methodPathObj.methodComment = obj[2];
                        methodPathArr.push(methodPathObj);
                    }

                    if (calleeBiz) {
                        globalCalleeGraph.graph = drawingGraph;
                        globalCalleeGraph.coll = data.methodIndexMap;
                        globalCalleeGraph.methodPathArr = methodPathArr;
                    } else {
                        globalCallerGraph.graph = drawingGraph;
                        globalCallerGraph.coll = data.methodIndexMap;
                        globalCallerGraph.methodPathArr = methodPathArr;
                    }

                    let currentWorkingCollectionData = calleeBiz ? globalCalleeGraph.coll : globalCallerGraph.coll;
                    drawCollectionModelContent(currentWorkingCollectionData);
                    $("#collection-calls-button").removeAttr("disabled");

                    $("#search-button").html('Search');
                    $("#search-button").removeAttr("disabled");
                },
                error: function (e) {
                    if (typeof globalCalleeGraph.graph !== 'undefined' && calleeBiz) {
                        globalCalleeGraph.graph.destroy();
                        globalCalleeGraph = {};
                    } else if (typeof globalCallerGraph.graph !== 'undefined' && !calleeBiz) {
                        globalCallerGraph.graph.destroy();
                        globalCallerGraph = {};
                    }

                    $("#search-button").html('Search');
                    $("#search-button").removeAttr("disabled");

                    $("#system-modal-info").html("Something happened..." + "<br/>" + JSON.stringify(e.responseJSON));
                    $('#system-modal').modal('show');
                }
            }
        )

    });

    /**
     * Q helper
     * find qualified name registration and business.
     */
    $("#name-search-button").on('click', function(){
        $("#name-search-button").prop("disabled", "disabled");
        $("#name-search-button").text('Matching...');

        $.ajax({
            url: '/getQualifiedNames',
            type: 'get',
            contentType: 'application/json',
            data:
                {
                    methodSimpleClass: $('#method-keyword-input-class').val().trim(),
                    methodSimpleMethod: $('#method-keyword-input-method').val().trim()
                },
            success: function (data) {
                $("#name-search-button").removeAttr("disabled");
                $("#name-search-button").text('Find qualified name');

                let rowCnt = 1;
                let htmlContent = '' +
                    '<table class="table table-sm table-striped table-hover">' +
                    '  <thead>' +
                    '    <tr>' +
                    '      <th scope="col">#</th>' +
                    '      <th scope="col">Operation</th>' +
                    '      <th scope="col">Method details</th>' +
                    '    </tr>' +
                    '  </thead>' +
                    '  <tbody>';

                for (let key in data.treeGraphData.children) {
                    htmlContent += '' +
                        '<tr>' +
                        '  <th scope="row">' + rowCnt + '</th>' +
                        '  <td>' + '<button type="button" class="btn btn-primary btn-sm" onclick="$(\'#keyword-input\').val(\''+ data.treeGraphData.children[key].methodQualifiedName +'\');$(\'#q-helper-modal\').modal(\'hide\');">SET Q</button>' + '</td>' +
                        '  <td style="white-space: pre-line">' + '<b>' + data.treeGraphData.children[key].methodQualifiedName + '</b>' +
                        '    <br/>' +
                             data.treeGraphData.children[key].methodComment +
                        '  </td>' +
                        '</tr>';
                    rowCnt++;
                }

                $("#q-helper-modal-info").html(htmlContent);

            },
            error: function (e) {
                $("#name-search-button").removeAttr("disabled");
                $("#name-search-button").text("Find qualified name");

                $("#q-helper-modal-info").html("");
                $("#system-modal-info").html("Something happened..." + "<br/>" + JSON.stringify(e.responseJSON));
                $("#system-modal").modal("show");
            }
        })
    });
});

/**
 * Method call collection modal
 * collect and draw the method call collection modal contents.
 *
 * @param workingCollectionData current showing graph's collection data
 */
function drawCollectionModelContent(workingCollectionData) {
    let totalTime = 0;
    let rowCnt = 1;
    let htmlContent = '' +
        '<table class="table table-sm table-striped">' +
        '  <thead>' +
        '    <tr>' +
        '      <th scope="col">#</th>' +
        '      <th scope="col" style="width: 90%;">Qualified name</th>' +
        '      <th scope="col">Call times</th>' +
        '    </tr>' +
        '  </thead>' +
        '  <tbody>';
    for (let key in workingCollectionData) {
        htmlContent += '' +
            '<tr>' +
            '  <th scope="row">' + rowCnt + '</th>' +
            '  <td>' + key + '</td>' +
            '  <td style="text-align: right;">' + workingCollectionData[key] + '</td>' +
            '</tr>';
        rowCnt++;
        totalTime += workingCollectionData[key];
    }
    $("#collection-calls-modal-info").html(htmlContent);
    $("#collection-calls-modal-info").html($("#collection-calls-modal-info").html() + "<hr/>" + "<span style='float: right;'>Total calls: " + "<b>" + totalTime + "</b></span>");
}

/**
 * Collapse node
 * collapse graph node when parents' node is under interaction.
 *
 * @param graphJson graph data
 * @param collapsed target node is collapsed
 */
function collapseNode(graphJson, collapsed) {
    for(let key in graphJson) {
        let element = graphJson[key];
        if(element.length > 0 && typeof(element) == "object" || typeof(element) == "object") {
            collapseNode(element);
        } else {
            if(key === 'collapsed'){
                graphJson['collapsed'] = collapsed;
            }
        }
    }
}

/**
 * Recover node origin color
 * recover the target node to its default color
 *
 * @param graph target graph
 * @param node target node
 */
function fillOriginColor(graph, node) {
    var methodType = node.getModel().methodType;
    var colour = null;
    if (methodType === 'BASE'){
        colour = 'rgba(198, 229, 255, 0.6)';
    } else if (methodType === 'LOOP-SRC'){
        colour = 'rgba(252, 244, 227, 0.6)';
    } else {
        colour = 'rgba(243, 246, 253, 0.6)';
    }
    node.get('group').getChildByIndex(1).attr("fill", colour);
    graph.refresh();
}

/**
 * Set node highlight color
 * set the target node with highlight background color.
 *
 * @param graph target graph
 * @param node target node
 */
function fillSearchColor(graph, node) {
    node.get('group').getChildByIndex(1).attr("fill", "rgba(255, 255, 15, 0.6)");
    graph.refresh();
}
