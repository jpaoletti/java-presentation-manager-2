<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${not empty param.value}">
    <c:if test="${empty param.ctx}">
        <a href="${cp}jpm/${param.entityId}/${param.objectId}/${param.operationId}" class="link-object-converter ${param.extraClass}">${param.value}</a>
    </c:if>
    <c:if test="${not empty param.ctx}">
        <a href="${cp}jpm/${param.entityId}!${param.ctx}/${param.objectId}/${param.operationId}" class="link-object-converter ${param.extraClass}">${param.value}</a>
    </c:if>
</c:if>