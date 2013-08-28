<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <body>
        <spring:message var="entityName" code="${entity.title}" text="${entity.title}" />
        <spring:message var="operationName" code="${operation.title}" arguments="${entityName}" text="Operation" />
        <%@include file="inc/header.jsp" %>
        <%@include file="inc/menu.jsp" %>
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
                                            <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">
                                                <spring:message code="jpm.list.search" text="Search" /> <span class="caret"></span>
                                            </button>
                                            <ul class="dropdown-menu" role="menu">
                                                <c:forEach items="${fieldSearchs}" var="fs">
                                                    <li role="presentation">
                                                        <a role="menuitem" tabindex="-1" href="javascript:openSearchModal('${fs.key.id}');"><spring:message code="jpm.field.${entity.id}.${fs.key.id}" text="${fs.key.id}" /></a>
                                                    </li>
                                                </c:forEach>
                                            </ul>
                                        </div>
                                        <div class="btn-group">
                                            <c:forEach items="${sessionEntityData.searchCriteria.definitions}" var="d" varStatus="st">
                                                <a type="button" href="<c:url value='/jpm/${entity.id}/removeSearch?i=${st.index}' />" class="btn btn-default removeSearchBtn">
                                                    ${d} <span class="glyphicon glyphicon-remove"></span>
                                                </a>
                                            </c:forEach>
                                        </div>
                                    </div>
                                    <c:if test="${paginable}">
                                        <div class="col-lg-3">
                                            <form action="" class="form-inline pull-right" role="form">
                                                <input type="hidden" name="entityId" value="${entityId}" />
                                                <input type="hidden" name="page" value="${paginatedList.page}" />
                                                <div class="form-group">
                                                    <label><spring:message code="jpm.list.pagesize" text="Page Size" /></label>
                                                    <input class="form-control page-size" type="number" min="0" max="100" value="${paginatedList.pageSize}" name="pageSize" style="width:60px;" />
                                                </div>
                                                <button type="submit" class="btn btn-default"><span class="glyphicon glyphicon-arrow-right"></span></button>
                                            </form>
                                        </div>
                                    </c:if>
                                </div>
                            </div>
                            <table class="table table-bordered table-compact">
                                <thead>
                                    <tr>
                                        <th style="width: 5px;"><i class="glyphicon glyphicon-cog"></i></th>
                                            <c:forEach items="${paginatedList.fields}" var="field">
                                            <th data-field="${field.id}"><jpm:field-title entity="${entity}" fieldId="${field.id}" /></th>
                                            </c:forEach>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${paginatedList.contents}" var="item">
                                        <tr data-id="${item.id}">
                                            <td>
                                                <div class="btn-group nowrap">
                                                    <c:forEach items="${item.operations}" var="o">
                                                        <a 
                                                            class="btn btn-mini btn-default confirm-${o.confirm}" 
                                                            title="<spring:message code="${o.title}" text="${o.title}" arguments="${entityName}" />"
                                                            href="<c:url value='/jpm/${entity.id}/${item.id}/${o.id}' />">
                                                            <i class="glyphicon jpmicon-${o.id}"></i>
                                                        </a>
                                                    </c:forEach>
                                                </div>
                                            </td>
                                            <c:forEach items="${item.values}" var="v">
                                                <td data-field="${v.key}">
                                                    <c:set var="convertedValue" value="${v.value}"/>
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
                            </table>
                            <div class="panel-footer row list-pagination">
                                <div class="col-lg-11">
                                    <%@include file="inc/default-paginator.jsp" %>
                                </div>
                                <div class="col-lg-1">
                                    <input class="form-control pull-right input-sm" disabled type="text" value="<spring:message code="jpm.list.total" text="Total: ${paginatedList.total}" arguments="${paginatedList.total}" />" />
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="modal fade" id="searchModal" tabindex="-1" role="dialog" aria-labelledby="searchModalLabel" aria-hidden="true">
            <form method="POST" id='addSearchForm' action="<c:url value='/jpm/${entity.id}/addSearch' />">
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
            <c:forEach items="${fieldSearchs}" var="fs">
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
        <script type="text/javascript">
            function openSearchModal(field) {
                $("#searchModal .modal-body").html($("#fieldSearchForm_" + field).html());
                $("#addSearchForm [name='fieldId']").val(field);
                $("#searchModal").modal("show").on("shown.bs.modal", function() {
                    $("#searchModal .modal-body").find("input").focus();
                });
            }
            jpmLoad(function() {
                //<c:forEach items="${paginatedList.fields}" var="f"><c:if test="${not empty f.align}">
                $("td[data-field='${f.id}']").css("text-align", "${f.align}");
                //</c:if><c:if test="${not empty f.width}">
                $("[data-field='${f.id}']").css("width", "${f.width}");
                //</c:if></c:forEach>
            });
        </script>
    </body>
</html>