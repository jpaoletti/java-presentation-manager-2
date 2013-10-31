<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
    <head>
        <%@include file="inc/default-head.jsp" %>
        <link href="${cp}static/css/bootstrap-editable.css?v=${jpm.appversion}" rel="stylesheet" type="text/css" />
    </head>
    <body>
        <c:set var="entityName" value="${entity.title}" />
        <spring:message var="operationName" code="${operation.title}" arguments="${entityName}" text="Operation" />
        <%@include file="inc/header.jsp" %>
        <jsp:include page="inc/menu/${currentHome}-menu.jsp" />
        <div id="container">
            <div id="content">
                <%@include file="inc/default-content-header.jsp" %>
                <%@include file="inc/default-breadcrumb.jsp" %>
                <div class="container-fluid">
                    <div class="row"><br/>
                        <div class="panel panel-default">
                            <div class="panel-heading operation-list">
                                <div class="row">
                                    <div class="col-lg-9">
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
                                                    <a type="button" href="${cp}jpm/${entityPath}/removeSearch?i=${st.index}" class="btn btn-default removeSearchBtn">
                                                        "${fieldTitle}" ${text}
                                                        <span class="glyphicon glyphicon-remove"></span>
                                                    </a>
                                                </c:if>
                                                <c:if test="${not empty owner}">
                                                    <a type="button" href="${cp}jpm/${owner.id}/${ownerId}/${entity.id}/removeSearch?i=${st.index}" class="btn btn-default removeSearchBtn">
                                                        "${fieldTitle}" ${text}
                                                        <span class="glyphicon glyphicon-remove"></span>
                                                    </a>
                                                </c:if>
                                            </c:forEach>
                                        </div>
                                    </div>
                                    <c:if test="${entity.paginable}">
                                        <div class="col-lg-3">
                                            <form action="" class="form-inline pull-right" role="form">
                                                <input type="hidden" name="entityId" value="${entityId}" />
                                                <input type="hidden" name="page" value="${paginatedList.page}" />
                                                <div class="form-group">
                                                    <label><spring:message code="jpm.list.pagesize" text="Page Size" /></label>
                                                    <input class="form-control page-size input-sm" type="number" min="0" max="100" value="${paginatedList.pageSize}" name="pageSize" style="width:60px;" />
                                                </div>
                                                <button type="submit" class="btn btn-default btn-sm"><span class="glyphicon glyphicon-arrow-right"></span></button>
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
                                        <c:forEach items="${paginatedList.fields}" var="field" varStatus="st">
                                            <th data-field="${field.id}" data-entity="${entity.id}" data-index='${st.index+1}'
                                                data-cp="${cp}jpm/${(empty owner)?'':(owner.id.concat("/").concat(ownerId).concat("/"))}"
                                                class="nowrap ${ (sessionEntityData.sort.field.id == field.id)?'sorted':''} ${field.sortable?'sortable':''}">
                                                <c:if test="${field.sortable}">
                                                    <span class="glyphicon ${(sessionEntityData.sort.field.id == field.id) ? (sessionEntityData.sort.asc?'glyphicon-sort-by-attributes':'glyphicon-sort-by-attributes-alt'):'glyphicon-sort'} pull-right"></span>
                                                </c:if>
                                                <jpm:field-title entity="${entity}" fieldId="${field.id}" />
                                            </th>
                                        </c:forEach>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${paginatedList.contents}" var="item">
                                        <tr data-id="${item.id}" class="instance-row ${item.highlight}">
                                            <th class="operation-list" style="width: ${fn:length(item.operations) * 30 + 20}px">
                                    <div class="btn-group nowrap">
                                        <c:if test="${not empty selectedOperations}">
                                            <input type="checkbox" class="pull-left selectable" data-id="${item.id}" />
                                        </c:if>
                                        <c:forEach items="${item.operations}" var="o">
                                            <a
                                                class="btn btn-mini btn-default confirm-${o.confirm}" 
                                                title="<spring:message code="${o.title}" text="${o.title}" arguments="${entityName}" />"
                                                href="${cp}jpm/${not empty owner.id ? owner.id.concat("."):""}${entityPath}/${item.id}/${o.id}">
                                                <i class="glyphicon jpmicon-${o.id}"></i>
                                            </a>
                                        </c:forEach>
                                    </div>
                                    </th>
                                    <c:forEach items="${item.values}" var="v">
                                        <td data-field="${v.key}">
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
                                                    <button class="btn btn-mini btn-default selected-operation" type="button" 
                                                            data-entity="${entity.id}" data-operation="${o.id}" data-confirm="${o.confirm}">
                                                        <i class="glyphicon jpmicon-${o.id}"></i>
                                                        <spring:message code="${o.title}" text="${o.title}" arguments="${entityName}" />
                                                    </button>
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
            </div>
        </div>
        <div class="modal fade" id="searchModal" tabindex="-1" role="dialog" aria-labelledby="searchModalLabel" aria-hidden="true">
            <c:if test="${empty owner}">
                <c:set var="addSearchUrl" value="${cp}jpm/${entityPath}/addSearch"/>
            </c:if>
            <c:if test="${not empty owner}">
                <c:set var="addSearchUrl" value="${cp}jpm/${owner.id}/${ownerId}/${entity.id}/addSearch"/>
            </c:if>
            <form method="POST" id='addSearchForm' action="${addSearchUrl}">
                <input type="hidden" name="entityId" value="${entityId}"/>
                <input type="hidden" name="fieldId" value=""/>
                <div class="modal-dialog modal-sm">
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
        <%@include  file="inc/footer.jsp" %>
        <%@include  file="inc/default-javascript.jsp" %>
        <script type='text/javascript' src="${cp}static/js/bootstrap-editable.min.js?v=${jpm.appversion}" ></script>
        <script type="text/javascript">
            function openSearchModal(field) {
                $("#searchModal .modal-body").html($("#fieldSearchForm_" + field).html());
                $("#addSearchForm [name='fieldId']").val(field);
                $("#searchModal").modal("show").on("shown.bs.modal", function() {
                    $("#searchModal .modal-body").find("input").focus();
                });
            }
            var sorting = false;
            jpmLoad(function() {
                $(".inline-edit").each(function() {
                    $(this).editable({
                        url: '${cp}jpm/${entityPath}/' + $(this).closest("tr").attr("data-id") + '/iledit',
                        send: "always", 
                        emptytext: "-"
                    });
                });
                $("#select_unselect_all").on("click", function() {
                    if ($(this).is(":checked")) {
                        $(".selectable").prop("checked", true);
                    } else {
                        $(".selectable").prop("checked", false);
                    }
                });
                $(".selected-operation").on("click", function() {
                    var instanceIds = $.map($('.selectable:checked'), function(n, i) {
                        return $(n).attr("data-id");
                    }).join(',');
                    if (instanceIds !== "") {
                        var btn = $(this);
                        //We simulate a link
                        var a = $("<a href='${cp}jpm/" + btn.attr("data-entity") + "/" + instanceIds + "/" + btn.attr("data-operation") + "' class='hide confirm-" + btn.attr("data-confirm") + "' />");
                        $("body").append(a);
                        a.trigger("click");
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
                $(document).on("keypress", function(e) {
                    if ($(e.target).closest("input")[0]) {return;}
                    if (e.which === parseInt("<spring:message code='jpm.list.shortcut.search' text='102' />")) { //search 'f'
                        $("#search-dropdown").dropdown('toggle').focus();
                    } else
                    if (e.which === parseInt("<spring:message code='jpm.list.shortcut.help' text='104' />")) { //sort 'h'
                        $('#help-btn').popover('toggle');
                    } else
                    if (e.which === parseInt("<spring:message code='jpm.list.shortcut.sort' text='115' />")) { //sort 's'
                        $(".sortable").each(function(i, v) {
                            var sortable = $(this);
                            var idx = sortable.attr("data-index") + ". ";
                            if (sortable.html().substring(0, idx.length) !== idx) {
                                sortable.prepend(idx);
                                setTimeout(function() {
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
                $("#search-dropdown").on("keypress", function(e) {
                    if (e.which > 48 && e.which <= 57) { // 1 - 9
                        openSearchModal($("#search-link-" + (e.which - 48)).attr("data-field"));
                    }
                });
            });
        </script>
    </body>
</html>