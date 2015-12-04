<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div id="${field}CollectionContainer">
    <c:if test="${not param.readonly}">
        <input type="text" id="${field}CollectionContainerSearchInput" value="" class="input" >
    </c:if>
    <input type="hidden" name="field_${field}" id="field_${field}" value="" />
    <div id="editAuthTree"></div>
</div>
<link href="${cp}static/css/jstree.css" rel="stylesheet">
<script type="text/javascript" src="${cp}static/js/jstree.min.js?v=${jpm.appversion}"></script>
<script type="text/javascript" src="${cp}jpm/security/i18n.js?v=${jpm.appversion}"></script>
<script type="text/javascript">
    var to = false;
    jpmLoad(function () {
        var readonly = ${param.readonly};
        var ids = "${param.value}".split(",");
        var root = [];
        var special = {'id': '-1', text: sec_i18n('jpm.security.authority.title'), children: [], state: {'opened': true}};
        var auths = {'id': '-2', text: sec_i18n('jpm.security.authority.jpmauth.title'), children: [], state: {'opened': true}};
        root.push(special);
        root.push(auths);
        $('<link href="${cp}static/css/jstree.css" rel="stylesheet">').appendTo("head");
        var f1 = $.ajax("${cp}jpm/jpm-entity-auth.json?textField=id", {
            dataType: "json",
            async: false,
            success: function (data) {
                $.each(data.results, function (i, item) {
                    if (!readonly || ($.inArray(item.id, ids) >= 0)) {
                        special.children.push({
                            'id': item.id,
                            'text': sec_i18n('jpm.security.authority.' + item.id) + "<span class='authKey'> [" + item.id + "]</span>",
                            'icon': "glyphicon glyphicon-chevron-right",
                            'state': {selected: !readonly && $.inArray(item.id, ids) >= 0}
                        });
                    }
                });
            }
        });

        var f2 = $.ajax("${cp}jpm/security/authorities.json?readonly=${param.readonly}&idGroup=${param.group}", {
                    dataType: "json",
                    async: false,
                    success: function (data) {
                        $.each(data.entities, function (i, item) {
                            var e = {
                                'id': item.key,
                                'text': item.name + "<span class='authKey'> [" + item.key + "]</span>",
                                children: [],
                                state: {'opened': true},
                                'icon': "glyphicon glyphicon-flag"
                            };
                            $.each(item.operations, function (i, oper) {
                                var o = {
                                    'id': oper.key,
                                    'text': oper.name + "<span class='authKey'> [" + oper.key + "]</span>",
                                    children: oper.fields.length > 0 ? [] : null,
                                    state: {'opened': false, selected: !readonly && $.inArray(oper.key, ids) >= 0},
                                    'icon': "glyphicon jpmicon-" + oper.id
                                };
                                if (oper.fields.length > 0) {
                                    $.each(oper.fields, function (i, field) {
                                        var f = {
                                            'id': field.key,
                                            'text': field.name + "<span class='authKey'> [" + field.key + "]</span>",
                                            'icon': "glyphicon glyphicon-chevron-right",
                                            'state': {selected: !readonly && $.inArray(field.key, ids) >= 0}
                                        };
                                        o.children.push(f);
                                    });
                                }
                                e.children.push(o);
                            });
                            auths.children.push(e);
                        });
                    }
                });

                $.when(f1, f2).done(function (a1, a2) {
                    $('#editAuthTree').jstree({
                        plugins: ["search" ${param.readonly=='false'?',"checkbox"':''}],
                        check_callback: true,
                        search: {
                            show_only_matches: true,
                            fuzzy: false,
                            case_sensitive: false,
                            search_leaves_only: false
                        },
                        checkbox: {
                            whole_node: false,
                            keep_selected_style: false,
                            real_checkboxes: true,
                            real_checkboxes_names: 'field_${field}'
                        },
                        "core": {
                            "multiple": true,
                            'data': root
                        }
                    }).on('changed.jstree', function (e, data) {
                        var selectedElmsIds = [];
                        var selectedElms = $('#editAuthTree').jstree("get_selected", true);
                        $.each(selectedElms, function (i, node) {
                            if (node.children.length === 0) {
                                selectedElmsIds.push(this.id);
                            }
                        });
                        $('#field_${field}').val(selectedElmsIds.join(","));
                    });
                    $('#${field}CollectionContainerSearchInput').keyup(function (e) {
                        if (to) {
                            clearTimeout(to);
                        }
                        to = setTimeout(function () {
                            var v = $('#${field}CollectionContainerSearchInput').val();
                            $('#editAuthTree').jstree(true).search(v);
                        }, 250);
                        e.preventDefault();
                    });
                });
            });
            $("#${field}CollectionContainerSearchInput").keypress(function (e) {
                if (e.which === 13) {
                    e.preventDefault();
                }
            });
</script>