<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html lang="${locale.language}">
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <jpm:jpm-body>
        <jpm:jpm-item-operation>
            <div class="row mb-3">
                <div class="col-lg-6 col-md-8 col-sm-12">
                    <input type="text" id="sysparamTreeSearch" class="form-control"
                           placeholder="<spring:message code='jpm.sysparam.tree.search' text='Search...' />" autocomplete="off" />
                </div>
            </div>
            <div class="row">
                <div class="col-lg-12">
                    <div id="sysparamTree"></div>
                </div>
            </div>
        </jpm:jpm-item-operation>
    </jpm:jpm-body>
    <script type="text/javascript" src="${cp}static/node_modules/jstree/dist/jstree.min.js?v=${jpm.appversion}"></script>
    <script type="text/javascript">
        jpmLoad(function () {
            // Inject the jstree CSS from JS so it also applies on SPA (AJAX) navigation, where
            // a <link> in the fragment's <head> would be ignored (only a full reload applies it).
            $('<link href="${cp}static/node_modules/jstree/dist/themes/default/style.min.css" rel="stylesheet">').appendTo("head");
            var searchTimeout = false;
            var root = ${treeJson};
            $('#sysparamTree').jstree({
                plugins: ["search"],
                search: {
                    show_only_matches: true,
                    show_only_matches_children: true,
                    fuzzy: false,
                    case_sensitive: false,
                    search_leaves_only: false
                },
                "core": {
                    "themes": {"dots": true, "icons": true},
                    'data': root
                }
            }).on('select_node.jstree', function (e, data) {
                var node = data.node;
                if (node.data && node.data.pid) {
                    var url = '${cp}jpm/sysparam/' + node.data.pid + '/setValue.exec';
                    if (typeof jpmNavigate === 'function') {
                        jpmNavigate(url);
                    } else {
                        window.location = url;
                    }
                }
            });
            $('#sysparamTreeSearch').keyup(function (e) {
                if (searchTimeout) {
                    clearTimeout(searchTimeout);
                }
                searchTimeout = setTimeout(function () {
                    $('#sysparamTree').jstree(true).search($('#sysparamTreeSearch').val());
                }, 250);
                e.preventDefault();
            });
            $('#sysparamTreeSearch').keypress(function (e) {
                if (e.which === 13) {
                    e.preventDefault();
                }
            });
        });
    </script>
</html>
