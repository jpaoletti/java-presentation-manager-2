<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html lang="${locale.language}">
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <jpm:jpm-body>
        <jpm:jpm-item-operation>
            <div class="row">
                <div class="col-lg-8 col-md-10">
                    <c:if test="${not menuEnabled}">
                        <div class="alert alert-warning small">
                            <i class="fas fa-info-circle"></i> <spring:message code="jpm.menu.preview.disabled" text="The dynamic menu is disabled: turn on the jpm.menu.dynamic parameter to use it." />
                        </div>
                    </c:if>
                    <input type="text" id="menuTreeSearch" class="form-control form-control-sm mb-2"
                           placeholder="<spring:message code='jpm.sysparam.tree.search' text='Search...' />" autocomplete="off" />
                    <div id="menuTree"></div>
                </div>
            </div>
        </jpm:jpm-item-operation>
    </jpm:jpm-body>
    <script type="text/javascript">
        jpmLoad(function () {
            // Inject the jstree CSS from JS so it also applies on SPA (AJAX) navigation
            $('<link href="${cp}static/node_modules/jstree/dist/themes/default/style.min.css" rel="stylesheet">').appendTo("head");
            var root = ${treeJson};

            function initTree() {
                $('#menuTree').jstree({
                    plugins: ["search"],
                    search: {show_only_matches: true, show_only_matches_children: true, case_sensitive: false},
                    "core": {"themes": {"dots": true, "icons": true}, 'data': root}
                }).on('select_node.jstree', function (e, data) {
                    var id = String(data.node.id).replace('m:', '');
                    jpmNavigate('${cp}jpm/jpmMenu/' + id + '/show');
                });
                var to = false;
                $('#menuTreeSearch').keyup(function (ev) {
                    if (to) { clearTimeout(to); }
                    to = setTimeout(function () { $('#menuTree').jstree(true).search($('#menuTreeSearch').val()); }, 250);
                    ev.preventDefault();
                }).keypress(function (ev) { if (ev.which === 13) { ev.preventDefault(); } });
            }

            if ($.fn.jstree) {
                initTree();
            } else {
                $.getScript('${cp}static/node_modules/jstree/dist/jstree.min.js').done(initTree);
            }
        });
    </script>
</html>
