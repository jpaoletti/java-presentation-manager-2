<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html lang="${locale.language}">
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <jpm:jpm-body>
        <jpm:jpm-item-operation>
            <%-- Whether the user may set a value; otherwise the editor stays read-only (browse only). --%>
            <c:set var="spCanEdit" value="false" />
            <security:authorize access="hasAnyAuthority('jpm.auth.operation.sysparam.setValue')">
                <c:set var="spCanEdit" value="true" />
            </security:authorize>
            <div class="row">
                <div class="col-lg-6 col-md-6">
                    <input type="text" id="sysparamTreeSearch" class="form-control form-control-sm mb-2"
                           placeholder="<spring:message code='jpm.sysparam.tree.search' text='Search...' />" autocomplete="off" />
                    <div id="sysparamTree"></div>
                </div>
                <div class="col-lg-6 col-md-6">
                    <div id="spEditor" class="border rounded p-3" style="display:none">
                        <h6 id="spTitle" class="mb-3 text-truncate"></h6>
                        <div id="spInput" class="mb-3"></div>
                        <button type="button" class="btn btn-primary btn-sm" id="spSave">
                            <i class="fas fa-save"></i> <spring:message code="jpm.button.save" text="Save" />
                        </button>
                        <button type="button" class="btn btn-outline-secondary btn-sm ms-1" id="spDefault" style="display:none">
                            <i class="fas fa-undo"></i> <spring:message code="jpm.sysparam.restoreDefault" text="Default" />
                        </button>
                        <span id="spMsg" class="ms-2 small"></span>
                    </div>
                    <div id="spEmpty" class="text-muted small p-3">
                        <i class="fas fa-hand-point-left"></i>
                        <spring:message code="jpm.entityparam.tree.pick" text="Select a parameter to edit its value." />
                    </div>
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
            var root = ${treeJson};
            var mask = '******';
            var canEdit = ${spCanEdit};
            var current = null;

            function esc(s) {
                return (s == null ? '' : String(s)).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
            }

            function buildInput(d) {
                if (d.secret) {
                    return '<input type="password" id="spValue" class="form-control" autocomplete="off" data-lpignore="true" placeholder="' + mask + '"/>'
                        + '<small class="text-muted"><spring:message code="jpm.sysparam.secretHint" text="Leave blank to keep the current secret; type a new value to replace it." /></small>';
                }
                if (d.type === 'BOOLEAN') {
                    var t = (d.cur === 'true');
                    return '<div class="form-check"><input class="form-check-input" type="radio" name="spv" id="spvt" value="true"' + (t ? ' checked' : '') + '/>'
                        + '<label class="form-check-label" for="spvt"><spring:message code="jpm.sysparam.bool.true" text="True" /></label></div>'
                        + '<div class="form-check"><input class="form-check-input" type="radio" name="spv" id="spvf" value="false"' + (!t ? ' checked' : '') + '/>'
                        + '<label class="form-check-label" for="spvf"><spring:message code="jpm.sysparam.bool.false" text="False" /></label></div>';
                }
                if (d.allowed && d.allowed.length) {
                    var o = '';
                    for (var i = 0; i < d.allowed.length; i++) {
                        o += '<option value="' + esc(d.allowed[i]) + '"' + (d.cur === d.allowed[i] ? ' selected' : '') + '>' + esc(d.allowed[i]) + '</option>';
                    }
                    return '<select id="spValue" class="form-select">' + o + '</select>';
                }
                if (d.type === 'INTEGER' || d.type === 'LONG' || d.type === 'DURATION') {
                    return '<input type="number" step="1" id="spValue" class="form-control" value="' + esc(d.cur) + '"/>';
                }
                if (d.type === 'DECIMAL' || d.type === 'DOUBLE') {
                    return '<input type="number" step="any" id="spValue" class="form-control" value="' + esc(d.cur) + '"/>';
                }
                if (d.type === 'JSON' || d.type === 'LIST') {
                    return '<textarea id="spValue" class="form-control" style="height:140px;">' + esc(d.cur) + '</textarea>';
                }
                return '<input type="text" id="spValue" class="form-control" value="' + esc(d.cur) + '"/>';
            }

            function readValue(d) {
                if (d.type === 'BOOLEAN') {
                    return $('input[name=spv]:checked').val();
                }
                return $('#spValue').val();
            }

            // Read-only rendering when the user lacks the setValue authority: show the current
            // value (masked for secrets) and a note, with no editable input.
            function buildReadonly(d) {
                var v = d.secret ? mask : (d.cur == null || d.cur === '' ? '&mdash;' : esc(d.cur));
                return '<div class="form-control-plaintext">' + v + '</div>'
                    + '<small class="text-muted"><i class="fas fa-lock"></i> '
                    + '<spring:message code="jpm.entityparam.tree.readonly" text="You do not have permission to modify this value." /></small>';
            }

            function openEditor(d) {
                current = d;
                $('#spMsg').removeClass('text-success text-danger').text('');
                $('#spTitle').text(d.name);
                $('#spInput').html(canEdit ? buildInput(d) : buildReadonly(d));
                $('#spSave').toggle(canEdit);
                $('#spDefault').toggle(canEdit && d.def != null && d.def !== '');
                $('#spEmpty').hide();
                $('#spEditor').show();
            }

            function initTree() {
                $('#sysparamTree').jstree({
                    plugins: ["search"],
                    search: {show_only_matches: true, show_only_matches_children: true, case_sensitive: false, search_leaves_only: false},
                    "core": {"themes": {"dots": true, "icons": true}, 'data': root}
                }).on('select_node.jstree', function (e, data) {
                    var node = data.node;
                    if (!node.data || !node.data.pid) {
                        return;
                    }
                    node.data.nodeId = node.id;
                    openEditor(node.data);
                });

                var to = false;
                $('#sysparamTreeSearch').keyup(function (ev) {
                    if (to) { clearTimeout(to); }
                    to = setTimeout(function () { $('#sysparamTree').jstree(true).search($('#sysparamTreeSearch').val()); }, 250);
                    ev.preventDefault();
                }).keypress(function (ev) { if (ev.which === 13) { ev.preventDefault(); } });
            }

            $('#spDefault').on('click', function () {
                if (current == null || current.def == null) { return; }
                var d = String(current.def);
                var $r = $('input[name=spv]');
                if ($r.length) { $r.prop('checked', false).filter('[value="' + d + '"]').prop('checked', true); return; }
                $('#spValue').val(d);
            });

            $('#spSave').on('click', function () {
                if (current == null || !canEdit) { return; }
                var val = readValue(current);
                var d = current;
                // Route through the sysparam setValue executor so validation, encryption (secrets),
                // history and cache eviction all apply — same as the standalone setValue form.
                var url = '${cp}jpm/sysparam/' + d.pid + '/setValue.exec';
                $('#sptmpform').remove();
                var form = $('<form method="POST" action="' + url + '" id="sptmpform" style="display:none"></form>');
                form.append($('<input type="hidden" name="value">').val(val == null ? '' : val));
                $('body').append(form);
                $('#spSave').prop('disabled', true);
                buildAjaxJpmFormObject(form, function (resp) {
                    form.remove();
                    $('#spSave').prop('disabled', false);
                    if (resp && resp.ok) {
                        // Saved: patch the selected leaf in place (no reload) so expanded/selected
                        // nodes are preserved. Recompute the displayed value client-side: secrets stay
                        // masked, everything else shows the (truncated) new value.
                        var shown = d.secret ? mask : (val == null ? '' : String(val));
                        if (shown.length > 60) { shown = shown.substring(0, 60) + '…'; }
                        var tree = $('#sysparamTree').jstree(true);
                        if (tree && d.nodeId) {
                            var $li = tree.get_node(d.nodeId, true);
                            if ($li && $li.length) {
                                $li.children('.jstree-anchor').find('span.text-muted').html('= ' + esc(shown));
                            }
                            var n = tree.get_node(d.nodeId);
                            if (n && n.data) { n.data.cur = d.secret ? '' : (val == null ? '' : val); }
                        }
                        d.cur = d.secret ? '' : (val == null ? '' : val);
                        if (d.secret) { $('#spValue').val(''); }
                        $('#spMsg').removeClass('text-danger').addClass('text-success')
                            .html('<i class="fas fa-check"></i> <spring:message code="jpm.edit.success" text="Saved" />');
                    } else {
                        // Validation/other error: keep the editor open and surface the messages.
                        if (typeof processFormResponse === 'function') { processFormResponse(resp); }
                    }
                }).submit();
            });

            if ($.fn.jstree) {
                initTree();
            } else {
                $.getScript('${cp}static/node_modules/jstree/dist/jstree.min.js').done(initTree);
            }
        });
    </script>
</html>
