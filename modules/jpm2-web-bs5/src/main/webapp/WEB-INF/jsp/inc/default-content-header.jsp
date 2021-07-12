<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${not empty operation}">
    <div id="content-header">
        <h3><span class="${operation.icon}"></span>  ${operationName}</h3>
        <h4>${entity.title} <i>${instance.iobject.object}</i></h4>
    </div>
</c:if>