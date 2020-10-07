<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <c:set var="entityName" value="${entity.title}" />
    <spring:message var="operationName" code="${operation.title}" arguments="${entityName}" text="Operation" />
    <jpm:jpm-body>
        <%@include file="inc/default-content-header.jsp" %>
        <%@include file="inc/default-breadcrumb.jsp" %>
        <div class="container-fluid" id="container-${fn:replace(contextualEntity,'!', '-')}-${operation.id}">
            <div class="row"><br/>
                <div class="panel panel-default">
                    <div class="panel-heading operation-list">
                        <div class="row">
                            <div class="col-lg-9 col-xs-6">
                                <div class="btn-group">
                                    <button id="search-dropdown" type="button" class="btn btn-primary btn-sm dropdown-toggle" data-toggle="dropdown">
                                        <spring:message code="jpm.list.search" text="Search" /> <span class="caret"></span>
                                    </button>
                                    <ul class="dropdown-menu" role="menu">
                                        <c:forEach items="${paginatedList.fieldSearchs}" var="fs" varStatus="st">
                                            <li role="presentation">
                                                <a id="search-link-${st.index+1}" role="menuitem" tabindex="-1" data-field='${fs.key.id}' href="javascript:openSearchModal('${fs.key.id}');">${st.index+1}. <spring:message code="jpm.field.${entity.id}.${fs.key.id}" text="${fs.key.id}" /></a>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </div>
                                <div class="btn-group">
                                    <c:forEach items="${sessionEntityData.searchCriteria.definitions}" var="d" varStatus="st">
                                        <spring:message var="fieldTitle" code="jpm.field.${entity.id}.${d.fieldId}" text="${d.fieldId}" />
                                        <spring:message var="text" code="${d.description.key}" arguments="${d.description.arguments}" argumentSeparator=";" />
                                        <c:if test="${empty owner}">
                                            <a type="button" href="${cp}jpm/${contextualEntity}/removeSearch?i=${st.index}" class="btn btn-default removeSearchBtn">
                                                "${fieldTitle}" ${text}
                                                <span class="glyphicon glyphicon-remove"></span>
                                            </a>
                                        </c:if>
                                        <c:if test="${not empty owner}">
                                            <!-- BUG NEED OWNER CONTEXT -->
                                            <a type="button" href="${cp}jpm/${owner.id}${entityContext}/${ownerId}/${contextualEntity}/removeSearch?i=${st.index}" class="btn btn-default removeSearchBtn">
                                                "${fieldTitle}" ${text}
                                                <span class="glyphicon glyphicon-remove"></span>
                                            </a>
                                        </c:if>
                                    </c:forEach>
                                </div>
                            </div>
                            <c:if test="${entity.paginable}">
                                <div class="col-lg-3 col-xs-6">
                                    <form action="" class="form-inline pull-right" role="form">
                                        <input type="hidden" name="entityId" value="${entityId}" />
                                        <input type="hidden" name="page" value="${paginatedList.page}" />
                                        <div class="form-group">
                                            <label class="sr-only" for="pageSize"><spring:message code="jpm.list.pagesize" text="Page Size" /></label>
                                            <div class="input-group">
                                                <div class="input-group-addon"><spring:message code="jpm.list.pagesize" text="Page Size" /></div>
                                                <input class="form-control page-size input-sm" type="number" min="0" max="100" value="${paginatedList.pageSize}" name="pageSize" style="width:60px;" />
                                                <div class="input-group-addon" id="pageSizeSubmit"><span class="glyphicon glyphicon-arrow-right"></span></div>
                                            </div>
                                        </div>
                                    </form>
                                </div>
                            </c:if>
                        </div>
                    </div>
                    <table class="table table-bordered table-compact jpm-list-table">
                        <thead>
                            <tr>
                                <th id='operation-column-header'>
                                    <c:if test="${not empty selectedOperations}">
                                        <input type="checkbox" class="pull-left" id="select_unselect_all" />
                                    </c:if>
                                    <a href="javascript:;" id="help-btn" data-toggle="popover" data-html="true"><i class="glyphicon glyphicon-cog"></i></a>
                                </th>
                                <th id='operation-column-header-info' class="visible-xs">
                                    <spring:message code="jpm.list.information" text="jpm.list.information" />
                                </th>
                                <c:forEach items="${paginatedList.fields}" var="field" varStatus="st">
                                    <th data-field="${field.id}" data-entity="${contextualEntity}" data-index='${st.index+1}'
                                        data-cp="${cp}jpm/${(empty owner)?'':(owner.id.concat("/").concat(ownerId).concat("/"))}"
                                        class="nowrap hidden-xs ${ (sessionEntityData.sort.field.id == field.id)?'sorted':''} ${field.sortable?'sortable':''}">
                                        <c:if test="${field.sortable}">
                                            <span class="sort-icon glyphicon ${(sessionEntityData.sort.field.id == field.id) ? (sessionEntityData.sort.asc?'glyphicon-sort-by-attributes':'glyphicon-sort-by-attributes-alt'):'glyphicon-sort'} pull-right"></span>
                                        </c:if>
                                        <jpm:field-title entity="${entity}" fieldId="${field.id}" />
                                    </th>
                                </c:forEach>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${paginatedList.contents}" var="item">
                                <tr data-id="${item.id}" class="instance-row ${item.highlight}">
                                    <th class="operation-list" style="width: ${(not compactOperations)?(fn:length(item.operations) * 30 + 25):((empty selectedOperations)?40:70)}px">
                                        <div class="btn-group nowrap">
                                            <c:if test="${not empty selectedOperations}">
                                                <input type="checkbox" class="pull-left selectable" data-id="${item.id}" />
                                            </c:if>
                                            <c:if test="${not compactOperations}">
                                                <c:forEach items="${item.operations}" var="o">
                                                    <jpm:operation-link operation="${o}" clazz="btn btn-xs btn-default" contextualEntity="${contextualEntity}" instanceId="${item.id}" entityName="${entityName}" />
                                                </c:forEach>
                                            </c:if>
                                            <c:if test="${compactOperations}">
                                                <div class="btn-group">
                                                    <button type="button" class="btn  btn-xs btn-default dropdown-toggle" data-toggle="dropdown">
                                                        <span class="glyphicon glyphicon-cog"></span> <span class="caret"></span>
                                                    </button>
                                                    <ul class="dropdown-menu" role="menu">
                                                        <c:forEach items="${item.operations}" var="o">
                                                            <li>
                                                                <jpm:operation-link operation="${o}" contextualEntity="${contextualEntity}" instanceId="${item.id}" entityName="${entityName}" title="true" />
                                                            </li>
                                                        </c:forEach>
                                                    </ul>
                                                </div>
                                            </c:if>
                                        </div>
                                    </th>
                                    <td class="visible-xs">
                                        <table class="table table-condensed borderless">
                                            <tbody>
                                                <c:forEach items="${item.values}" var="v">
                                                    <c:set var="convertedValue" value="${v.value}"/>
                                                    <c:set var="field" value="${v.key}" scope="request" />
                                                    <tr>
                                                        <th><jpm:field-title entity="${entity}" fieldId="${field}" /></th>
                                                        <td>
                                                            <c:if test="${fn:startsWith(convertedValue, '@page:')}">
                                                                <jsp:include page="converter/${fn:replace(convertedValue, '@page:', '')}" flush="true" />
                                                            </c:if>
                                                            <c:if test="${not fn:startsWith(convertedValue, '@page:')}">
                                                                ${convertedValue}
                                                            </c:if>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </td>
                                    <c:forEach items="${item.values}" var="v">
                                        <td data-field="${v.key}" class="hidden-xs">
                                            <c:set var="convertedValue" value="${v.value}"/>
                                            <c:set var="field" value="${v.key}" scope="request" />
                                            <c:if test="${fn:startsWith(convertedValue, '@page:')}">
                                                <jsp:include page="converter/${fn:replace(convertedValue, '@page:', '')}" flush="true" />
                                            </c:if>
                                            <c:if test="${not fn:startsWith(convertedValue, '@page:')}">
                                                ${convertedValue}
                                            </c:if>
                                        </td>
                                    </c:forEach>
                                </tr>
                            </c:forEach>
                        </tbody>
                        <c:if test="${not empty selectedOperations}">
                            <tfoot>
                                <tr>
                                    <td colspan="100">
                                        <img src="${cp}static/img/arrow_ltr.png" alt="selected" class="pull-left" />
                                        <c:forEach var="o" items="${selectedOperations}">
                                            <jpm:operation-link operation="${o}" clazz="btn btn-xs btn-default selected-operation" contextualEntity="${contextualEntity}" instanceId="@@" entityName="${entityName}" title="true" />
                                        </c:forEach>
                                    </td>
                                </tr>
                            </tfoot>
                        </c:if>
                    </table>
                    <div class="panel-footer row list-pagination">
                        <div class="col-lg-11">
                            <%@include file="inc/default-paginator.jsp" %>
                        </div>
                        <div class="col-lg-1">
                            <c:if test="${entity.countable}">
                                <span class="label label-default"><spring:message code="jpm.list.total" text="Total: ${paginatedList.total}" arguments="${paginatedList.total}" /></span>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="modal fade" id="searchModal" tabindex="-1" role="dialog" aria-labelledby="searchModalLabel" aria-hidden="true">
            <c:if test="${empty owner}">
                <c:set var="addSearchUrl" value="${cp}jpm/${contextualEntity}/addSearch"/>
            </c:if>
            <c:if test="${not empty owner}">
                <c:set var="addSearchUrl" value="${cp}jpm/${owner.id}${entityContext}/${ownerId}/${contextualEntity}/addSearch"/>
            </c:if>
            <form method="POST" id='addSearchForm' action="${addSearchUrl}">
                <input type="hidden" name="entityId" value="${entityId}"/>
                <input type="hidden" name="fieldId" value=""/>
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                            <h4 class="modal-title"><spring:message code="jpm.list.modalsearch.title" text="New Search" /></h4>
                        </div>
                        <div class="modal-body">
                            ...
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="jpm.list.modalsearch.close" text="Close" /></button>
                            <button type="submit" class="btn btn-primary"><spring:message code="jpm.list.modalsearch.ok" text="Ok" /></button>
                        </div>
                    </div>
                </div>
            </form>
        </div>
        <div class="hide" id='fieldSearchForms'>
            <c:forEach items="${paginatedList.fieldSearchs}" var="fs">
                <div id='fieldSearchForm_${fs.key.id}'>
                    <c:if test="${fn:startsWith(fs.value, '@page:')}">
                        <jsp:include page="searcher/${fn:replace(fs.value, '@page:', '')}" flush="true" />
                    </c:if>
                    <c:if test="${not fn:startsWith(fs.value, '@page:')}">
                        ${fs.value}
                    </c:if>
                </div>
            </c:forEach>
        </div>
    </jpm:jpm-body>
    <script type='text/javascript' src="${cp}static/js/jquery.jeditable.min.js?v=${jpm.appversion}" ></script>
    <script type="text/javascript">
        var sorting = false;
        jpmLoad(function () {
            $("#pageSizeSubmit").on("click", function () {
                $(this).parents("form").trigger('submit');
            });
            $(".inline-edit").each(function () {
                $(this).editable('${cp}jpm/${contextualEntity}/' + $(this).closest("tr").attr("data-id") + '/iledit', {
                    placeholder: "-",
                    submitdata: {
                        name: $(this).attr("data-name")
                    }
                });
            });
            $("#select_unselect_all").on("click", function () {
                if ($(this).is(":checked")) {
                    $(".selectable").prop("checked", true);
                } else {
                    $(".selectable").prop("checked", false);
                }
            });
            $(".selected-operation").on("click", function (e) {
                e.preventDefault();
                var instanceIds = $.map($('.selectable:checked'), function (n, i) {
                    return $(n).attr("data-id");
                }).join(',');
                if (instanceIds !== "") {
                    var btn = $(this);
                    var confirm = btn.attr("data-confirm") === "true";
                    var link = $(this).attr("href").replace("@@", instanceIds);
                    if (confirm) {
                        //We simulate a link
                        var a = $("<a href='" + link + "' class='hide confirm-" + btn.attr("data-confirm") + "' />");
                        $(document).append(a);
                        a.trigger("click");
                    } else {
                        document.location = link;
                    }
                }
            });
            $("#help-btn").popover({
                title: "<spring:message code='jpm.list.shortcut.help.title' text='Help' />",
                content: "<spring:message code='jpm.list.shortcut.help.content' text='<ul><li>Press \'f\' to search</li><li>Press \'s\' to sort</li><li>Press \'h\' for help</li></ul>' />"
            });
            //<c:forEach items="${paginatedList.fields}" var="f"><c:if test="${not empty f.align}">
            $("td[data-field='${f.id}']").css("text-align", "${f.align}");
            //</c:if><c:if test="${not empty f.width}">
            $("td[data-field='${f.id}'], th[data-field='${f.id}']").css("width", "${f.width}");
            //</c:if></c:forEach>
            $(document).on("keypress", function (e) {
                if ($(e.target).closest("input")[0]) {
                    return;
                }
                if (e.which === parseInt("<spring:message code='jpm.list.shortcut.search' text='102' />")) { //search 'f'
                    $("#search-dropdown").dropdown('toggle').trigger('focus');
                } else
                if (e.which === parseInt("<spring:message code='jpm.list.shortcut.help' text='104' />")) { //sort 'h'
                    $('#help-btn').popover('toggle');
                } else
                if (e.which === parseInt("<spring:message code='jpm.list.shortcut.sort' text='115' />")) { //sort 's'
                    $(".sortable").each(function (i, v) {
                        var sortable = $(this);
                        var idx = sortable.attr("data-index") + ". ";
                        if (sortable.html().substring(0, idx.length) !== idx) {
                            sortable.prepend(idx);
                            setTimeout(function () {
                                sortable.html(sortable.html().substring(idx.length));
                                sorting = false;
                            }, 3000);
                        }
                    });
                    sorting = true;
                } else if (sorting && e.which > 48 && e.which <= 57) {
                    if (e.which > 48 && e.which <= 57) { // 1 - 9
                        $(".sortable[data-index='" + (e.which - 48) + "']").trigger("click");
                    }
                }

            });
            $("#search-dropdown").on("keypress", function (e) {
                if (e.which > 48 && e.which <= 57) { // 1 - 9
                    openSearchModal($("#search-link-" + (e.which - 48)).attr("data-field"));
                }
            });
        });
    </script>
</html>