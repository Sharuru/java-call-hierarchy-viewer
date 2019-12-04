$(document).ready(function () {

    // Debug
    //$("#keyword-input").val('jp.co.toyotsu.jast.lx.lx09.web.lx09s0010.Lx09S0010Controller.lx09U0020Search(Lx09U0010Criteria, Lx09U0020Form, BindingResult, Pageable, Model)');

    // Inspector
    $('#info-func-btn-1').tooltip({
        'trigger': 'hover',
        'placement': 'bottom',
        'title': 'Set qualified name as keyword'
    });

    $("#info-func-btn-1").on('click', function () {
        $("#keyword-input").val($('#info-qualified-name').text());
    });

    $('#info-func-btn-2').tooltip({
        'trigger': 'hover',
        'placement': 'bottom',
        'title': 'Copy class filename'
    });

    $('#info-func-btn-3').tooltip({
        'trigger': 'hover',
        'placement': 'bottom',
        'title': 'Copy method without parameters'
    });

    $("#info-area").draggable({cursor: "move", handle: "div#info-area-title"});
    $("#info-area").resizable();

    $("#reset-handler").on('click', function () {
        $("#info-area").animate({
            left: "80px",
            top: "240px",
            width: "30rem",
            height: "160px"
        }, 500);
    });

    // Function
    if (localStorage.getItem("showTip") === null) {
        $('#keyword-input').tooltip({
            'trigger': 'focus',
            'placement': 'bottom',
            'html': true,
            'title': '<div>Qualified name is something like: <b>com.example.foobar.getMethod(Type)</b><br/>' +
                'You can get it from Eclipse or STS in the context menu after selecting the method:</div></br>' +
                '<img align="center" src="/static/get-qualified-name.png"> </br></br>' +
                '<div><a id="do-not-show-tip" href="#">Don\'t show this again.</a></div>'
        });
        $('#keyword-input').on('shown.bs.tooltip', function () {
            $("#do-not-show-tip").on('click', function () {
                $('#keyword-input').tooltip('disable');
                localStorage.setItem("showTip", "false");
            });
        });
    }

    $("#collection-button").on('click', function () {
        $('#collection-modal').modal('show');
    });

    $("#reset-layout-button").on('click', function () {
        if ($("#pills-caller-tab").hasClass('active') === true && typeof globalCallerGraph !== 'undefined' && typeof globalCallerGraph.graph !== 'undefined' ) {
            globalCallerGraph.graph.refreshLayout(true);
        } else if($("#pills-callee-tab").hasClass('active') === true && typeof globalCalleeGraph !== 'undefined' && typeof globalCalleeGraph.graph !== 'undefined' ){
            globalCalleeGraph.graph.refreshLayout(true);
        }
        $("#info-area").animate({
            left: "80px",
            top: "240px",
            width: "30rem",
            height: "160px"
        }, 500);
    });

    $('a[data-toggle="pill"]').on('shown.bs.tab', function (e) {
        $("#collection-button").prop("disabled", "disabled");
        if (e.target.id === 'pills-caller-tab' && typeof globalCallerGraph !== 'undefined' && typeof globalCallerGraph.coll !== 'undefined') {
            drawCollectionModelContent(globalCallerGraph.coll);
            $("#collection-button").removeAttr("disabled");
        } else if (e.target.id === 'pills-callee-tab' && typeof globalCalleeGraph !== 'undefined' && typeof globalCalleeGraph.coll !== 'undefined') {
            drawCollectionModelContent(globalCalleeGraph.coll);
            $("#collection-button").removeAttr("disabled");
        }
    });

    // Graph
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

    $("#search-button").on('click', function () {

        $("#collection-button").prop("disabled", "disabled");
        $("#search-button").prop("disabled", "disabled");
        $("#search-button").html('<span class="spinner-border spinner-border-sm" role="status"></span>Searching...');

        if ($("#pills-caller-tab").hasClass('active') === true) {
            apiLink = '/getCallerMethodInfo';
            calleeBiz = false;
        } else {
            apiLink = '/getCalleeMethodInfo';
            calleeBiz = true;
        }
        $.ajax({
                url: apiLink,
                type: "get",
                contentType: "application/json",
                data:
                    {
                        methodQualifiedName: $("#keyword-input").val().trim()
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
                    const minimap = new Minimap({
                        size: [160, 160],
                        className: 'minimap',
                        type: 'delegate',
                    });

                    let containerId = calleeBiz ? 'callee-graph-container' : 'caller-graph-container';

                    let drawingGraph = new G6.TreeGraph({
                        container: containerId,
                        width: document.getElementById(containerId).scrollWidth,
                        height: document.getElementById(containerId).scrollHeight || window.innerHeight * 0.7,
                        defaultNode: {
                            shape: 'method-card',
                            anchorPoints: [[0, 0.5], [1, 0.5]]
                        },
                        defaultEdge: {
                            shape: 'cubic-horizontal'
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
                        const classname = ev.target.get('className');
                        const item = ev.item;
                        if (classname === '_id-hover-layer' && item) {
                            $("#info-qualified-name").text(item.getModel().methodQualifiedName);
                            $("#info-method-path").text(item.getModel().uiMethodPath);
                            $("#info-method-comment").text(item.getModel().uiMethodComment);
                            $("#info-content").animate({scrollTop: 0}, "fast");
                        }
                    });

                    drawingGraph.refreshLayout(true);

                    if (calleeBiz) {
                        globalCalleeGraph.graph = drawingGraph;
                        globalCalleeGraph.coll = data.methodIndexMap;
                    } else {
                        globalCallerGraph.graph = drawingGraph;
                        globalCallerGraph.coll = data.methodIndexMap;
                    }

                    let workingCollDat = calleeBiz ? globalCalleeGraph.coll : globalCallerGraph.coll;
                    drawCollectionModelContent(workingCollDat);
                    $("#collection-button").removeAttr("disabled");

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
});

function drawCollectionModelContent(workingCollDat) {
    let totalTime = 0;
    let rowCnt = 1;
    let htmlContent = '<table class="table table-sm table-striped">' +
        '  <thead>' +
        '    <tr>' +
        '      <th scope="col">#</th>' +
        '      <th scope="col" style="width: 90%;">Method qualified name</th>' +
        '      <th scope="col">Call times</th>' +
        '    </tr>' +
        '  </thead>' +
        '  <tbody>';
    for (let key in workingCollDat) {
        htmlContent += '<tr>' +
            '      <th scope="row">' + rowCnt + '</th>' +
            '      <td>' + key + '</td>' +
            '      <td style="text-align: right;">' + workingCollDat[key] + '</td>' +
            '    </tr>';
        rowCnt++;
        totalTime += workingCollDat[key];
    }
    $("#collection-modal-info").html(htmlContent);
    $("#collection-modal-info").html($("#collection-modal-info").html() + "<hr/>" + "<span style='float: right;'>Total calls: " + "<b>" + totalTime + "</b></span>");
}

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
