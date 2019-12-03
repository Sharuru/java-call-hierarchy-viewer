$(document).ready(function(){

    $("#keyword-input").val('jp.co.toyotsu.jast.lx.lx09.web.lx09s0010.Lx09S0010Controller.lx09U0020Search(Lx09U0010Criteria, Lx09U0020Form, BindingResult, Pageable, Model)');

    $('#info-func-btn-1').tooltip({
        'trigger': 'hover',
        'placement': 'bottom',
        'title': 'Set qualified name as keyword'
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

    $("#info-area").draggable({cursor: "move", handle: "span#drag-handler"});
    $("#reset-handler").on('click', function () {
        $( "#info-area" ).animate({
            left: "80px",
            top: "240px"
        }, 500 );
    });
    $("#collection-button").on('click', function () {
        $('#system-modal').modal('show');
    });

    $("#info-func-btn-1").on('click', function () {
        $("#keyword-input").val($('#info-qualified-name').text());
    });



    G6.registerNode(
        'method-card',
        {
            drawShape:(cfg, group) =>{
                const container = group.addShape('rect', {
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
        },'rect');



    let globalCalleeGraph = null;
    let globalCallerGraph = null;
    let calleeBiz = true;
    let apiLink = null;

    $("#search-button").on('click', function () {

        $("#collection-button").prop("disabled", "disabled");

        if($("#pills-caller-tab").hasClass('active') === true){
            apiLink = '/getCallerMethodInfo';
            calleeBiz = false;
        }else{
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
                    if(globalCalleeGraph !== null && calleeBiz){
                        globalCalleeGraph.destroy();
                        globalCalleeGraph = null;
                    }else if(globalCallerGraph !== null && !calleeBiz){
                        globalCallerGraph.destroy();
                        globalCallerGraph = null;
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
                            anchorPoints: [[ 0, 0.5 ], [ 1, 0.5 ]]
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
                                    return true;
                                }
                            }, 'drag-canvas', 'zoom-canvas' ]
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
                        if(classname === '_id-hover-layer' && item){
                            $("#info-qualified-name").text(item.getModel().methodQualifiedName);
                            $("#info-method-path").text(item.getModel().uiMethodPath);
                            $("#info-method-comment").text(item.getModel().uiMethodComment);
                            $("#info-content").animate({ scrollTop: 0 }, "fast");
                        }
                    });

                    drawingGraph.refreshLayout(true);

                    if(calleeBiz){
                        globalCalleeGraph = drawingGraph;
                    }else{
                        globalCallerGraph = drawingGraph;
                    }

                    let totalTime = 0;
                    for(let key in data.calleeMethodIndexMap){
                        $("#system-modal-info").html($("#system-modal-info").html() + "<br/>" + "Name: " + "<b>" + key + "</b>" +", Call times: " + "<b>" + data.calleeMethodIndexMap[key] + "<b>");
                        totalTime += data.calleeMethodIndexMap[key];
                    }
                    $("#system-modal-info").html($("#system-modal-info").html() + "<br/>" + "Total calls: " + "<b>" + totalTime +"</b>");

                    $("#collection-button").removeAttr("disabled");

                },
                error: function (e) {
                    if(globalCalleeGraph !== null && calleeBiz){
                        globalCalleeGraph.destroy();
                        globalCalleeGraph = null;
                    }else if(globalCallerGraph !== null && !calleeBiz){
                        globalCallerGraph.destroy();
                        globalCallerGraph = null;
                    }

                    console.error('Error happened: ' + JSON.stringify(e.responseJSON));
                    $("#system-modal-info").text(JSON.stringify(e.responseJSON));
                    $('#system-modal').modal('show');

                }
            }
        )

    });

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


});











