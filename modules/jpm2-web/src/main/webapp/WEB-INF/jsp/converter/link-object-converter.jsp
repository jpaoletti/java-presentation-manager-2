<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${not empty param.value}">
    <a href="${cp}jpm/${param.entityId}/${param.objectId}/${param.operationId}">${param.value}</a>
</c:if>