<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<c:set var="epJsonKey" value="epTreeJson_${field}" />
<c:set var="epTreeJson" value="${requestScope[epJsonKey]}" />
<%-- Whether the current user may actually set a value; if not, the editor stays read-only
     (the tree still lets them browse values). Mirrors the setEntityParameterValue op auth key. --%>
<c:set var="epCanEdit" value="false" />
<security:authorize access="hasAnyAuthority('jpm.auth.operation.${param.childEntity}.setEntityParameterValue')">
    <c:set var="epCanEdit" value="true" />
</security:authorize>
<security:authorize access="hasAnyAuthority('${param.weakAuth}')">
    <div class="mb-2">
        <a class="btn btn-info btn-sm text-light" href="${cp}jpm/${contextualEntity}/${param.ownerId}/${param.childEntity}${param.context}/list">
            <span class="${param.btnIcon}"></span>&nbsp;<spring:message code='${param.btnText}' text='Parameters' />
        </a>
    </div>
</security:authorize>
<div class="row" id="epwrap-${field}">
    <div class="col-lg-6 col-md-6">
        <input type="text" id="epsearch-${field}" class="form-control form-control-sm mb-2"
               placeholder="<spring:message code='jpm.entityparam.tree.search' text='Search...' />" autocomplete="off"/>
        <div id="eptree-${field}"></div>
    </div>
    <div class="col-lg-6 col-md-6">
        <div id="epeditor-${field}" class="border rounded p-3" style="display:none">
            <h6 id="eptitle-${field}" class="mb-3 text-truncate"></h6>
            <div id="epinput-${field}" class="mb-3"></div>
            <button type="button" class="btn btn-primary btn-sm" id="epsave-${field}">
                <i class="fas fa-save"></i> <spring:message code="jpm.button.save" text="Save" />
            </button>
            <button type="button" class="btn btn-outline-secondary btn-sm ms-1" id="epdefault-${field}" style="display:none">
                <i class="fas fa-undo"></i> <spring:message code="jpm.sysparam.restoreDefault" text="Default" />
            </button>
            <span id="epmsg-${field}" class="ms-2 small"></span>
        </div>
        <div id="epempty-${field}" class="text-muted small p-3">
            <i class="fas fa-hand-point-left"></i>
            <spring:message code="jpm.entityparam.tree.pick" text="Select a parameter to edit its value." />
        </div>
    </div>
</div>
<script type="text/javascript">
    jpmLoad(function () {
        var field = '${field}';
        var childEntity = '${param.childEntity}';
        var root = ${empty epTreeJson ? '[]' : epTreeJson};
        var mask = '******';
        var canEdit = ${epCanEdit};
        var current = null;

        // Widen the field cell (drop the label column, like the weak converter).
        $("#control-group-" + field).find(".col-lg-4").remove();
        $("#control-group-" + field).find(".col-lg-8").removeClass("col-lg-8").addClass("col-lg-12");

        function esc(s) {
            return (s == null ? '' : String(s)).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
        }

        function buildInput(d) {
            if (d.secret) {
                return '<input type="password" id="epvalue-' + field + '" class="form-control" autocomplete="off" data-lpignore="true" placeholder="' + mask + '"/>'
                    + '<small class="text-muted"><spring:message code="jpm.sysparam.secretHint" text="Leave blank to keep the current secret; type a new value to replace it." /></small>';
            }
            if (d.type === 'BOOLEAN') {
                var t = (d.cur === 'true');
                return '<div class="form-check"><input class="form-check-input" type="radio" name="epv-' + field + '" id="epvt-' + field + '" value="true"' + (t ? ' checked' : '') + '/>'
                    + '<label class="form-check-label" for="epvt-' + field + '"><spring:message code="jpm.sysparam.bool.true" text="True" /></label></div>'
                    + '<div class="form-check"><input class="form-check-input" type="radio" name="epv-' + field + '" id="epvf-' + field + '" value="false"' + (!t ? ' checked' : '') + '/>'
                    + '<label class="form-check-label" for="epvf-' + field + '"><spring:message code="jpm.sysparam.bool.false" text="False" /></label></div>';
            }
            if (d.allowed && d.allowed.length) {
                var o = '';
                for (var i = 0; i < d.allowed.length; i++) {
                    o += '<option value="' + esc(d.allowed[i]) + '"' + (d.cur === d.allowed[i] ? ' selected' : '') + '>' + esc(d.allowed[i]) + '</option>';
                }
                return '<select id="epvalue-' + field + '" class="form-select">' + o + '</select>';
            }
            if (d.type === 'INTEGER' || d.type === 'LONG' || d.type === 'DURATION') {
                return '<input type="number" step="1" id="epvalue-' + field + '" class="form-control" value="' + esc(d.cur) + '"/>';
            }
            if (d.type === 'DECIMAL' || d.type === 'DOUBLE') {
                return '<input type="number" step="any" id="epvalue-' + field + '" class="form-control" value="' + esc(d.cur) + '"/>';
            }
            if (d.type === 'JSON' || d.type === 'LIST') {
                return '<textarea id="epvalue-' + field + '" class="form-control" style="height:140px;">' + esc(d.cur) + '</textarea>';
            }
            return '<input type="text" id="epvalue-' + field + '" class="form-control" value="' + esc(d.cur) + '"/>';
        }

        function readValue(d) {
            if (d.type === 'BOOLEAN') {
                return $('input[name=epv-' + field + ']:checked').val();
            }
            return $('#epvalue-' + field).val();
        }

        // Read-only rendering when the user lacks the setEntityParameterValue authority: show the
        // current value (masked for secrets) and a note, with no editable input.
        function buildReadonly(d) {
            var v = d.secret ? mask : (d.cur == null || d.cur === '' ? '&mdash;' : esc(d.cur));
            return '<div class="form-control-plaintext">' + v + '</div>'
                + '<small class="text-muted"><i class="fas fa-lock"></i> '
                + '<spring:message code="jpm.entityparam.tree.readonly" text="You do not have permission to modify this value." /></small>';
        }

        function openEditor(d) {
            current = d;
            $('#epmsg-' + field).removeClass('text-success text-danger').text('');
            $('#eptitle-' + field).text(d.name);
            $('#epinput-' + field).html(canEdit ? buildInput(d) : buildReadonly(d));
            $('#epsave-' + field).toggle(canEdit);
            $('#epdefault-' + field).toggle(canEdit && d.def != null && d.def !== '');
            $('#epempty-' + field).hide();
            $('#epeditor-' + field).show();
        }

        function initTree() {
            $('#eptree-' + field).jstree({
                plugins: ["search"],
                search: {show_only_matches: true, show_only_matches_children: true, case_sensitive: false},
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
            $('#epsearch-' + field).keyup(function (ev) {
                if (to) { clearTimeout(to); }
                to = setTimeout(function () { $('#eptree-' + field).jstree(true).search($('#epsearch-' + field).val()); }, 250);
                ev.preventDefault();
            }).keypress(function (ev) { if (ev.which === 13) { ev.preventDefault(); } });
        }

        $('#epdefault-' + field).on('click', function () {
            if (current == null || current.def == null) { return; }
            var d = String(current.def);
            var $r = $('input[name=epv-' + field + ']');
            if ($r.length) { $r.prop('checked', false).filter('[value="' + d + '"]').prop('checked', true); return; }
            $('#epvalue-' + field).val(d);
        });

        $('#epsave-' + field).on('click', function () {
            if (current == null || !canEdit) { return; }
            var val = readValue(current);
            var d = current;
            var url = '${cp}jpm/' + childEntity + '/' + d.pid + '/setEntityParameterValue.exec';
            $('#eptmpform-' + field).remove();
            var form = $('<form method="POST" action="' + url + '" id="eptmpform-' + field + '" style="display:none"></form>');
            form.append($('<input type="hidden" name="value">').val(val == null ? '' : val));
            $('body').append(form);
            $('#epsave-' + field).prop('disabled', true);
            buildAjaxJpmFormObject(form, function (resp) {
                form.remove();
                if (resp && resp.ok) {
                    // Value saved: reload the parent show in place (no redirect) so the tree is rebuilt fresh.
                    jpmNavigate(window.location.href, {push: false, replace: true});
                } else {
                    // Validation/other error: keep the editor open and surface the messages.
                    $('#epsave-' + field).prop('disabled', false);
                    if (typeof processFormResponse === 'function') { processFormResponse(resp); }
                }
            }).submit();
        });

        $('<link rel="stylesheet" href="${cp}static/node_modules/jstree/dist/themes/default/style.min.css">').appendTo("head");
        if ($.fn.jstree) {
            initTree();
        } else {
            $.getScript('${cp}static/node_modules/jstree/dist/jstree.min.js').done(initTree);
        }
    });
</script>
