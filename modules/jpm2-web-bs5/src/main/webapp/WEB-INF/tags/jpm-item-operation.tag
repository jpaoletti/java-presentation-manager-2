<%@tag description="Item operation container" pageEncoding="UTF-8"%>
<%@include file="../jsp/inc/default-taglibs.jsp" %>
<c:set var="entityName" value="${entity.title}" />
<c:if test="${not empty operation}">
    <spring:message var="operationName" code="${operation.title}" arguments="${entityName}" />
</c:if>
<%@include file="../jsp/inc/default-itemop-header.jsp" %>
<div class="container-fluid" id="container-${fn:replace(contextualEntity,'!', '-')}-${operation.id}">
    <div class="row"><br/>
        <div class="col-lg-12">
            <div id="jpmMainPanel">
                <jsp:doBody />
            </div>
        </div>
    </div>
</div>