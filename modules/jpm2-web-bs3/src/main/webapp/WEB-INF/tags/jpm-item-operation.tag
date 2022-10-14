<%@tag description="Item operation container" pageEncoding="UTF-8"%>
<%@include file="../jsp/inc/default-taglibs.jsp" %>
<c:set var="entityName" value="${entity.title}" />
<c:if test="${not empty operation}">
    <spring:message var="operationName" code="${operation.title}" arguments="${entityName}" />
</c:if>
<%@include file="../jsp/inc/default-content-header.jsp" %>
<%@include file="../jsp/inc/default-breadcrumb.jsp" %>
<div class="container-fluid">
    <div class="row"><br/>
        <div class="col-12">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <%@include file="../jsp/inc/item-operations.jsp" %>
                </div>
                <div class="panel-body">
                    <div class="row">
                        <div class="col-md-6">
                            <jsp:doBody />
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>