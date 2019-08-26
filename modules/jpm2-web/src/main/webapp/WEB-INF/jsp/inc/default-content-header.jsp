<div id="content-header" class="page-header">
    <h1 class="hidden-xs">${operationName}</h1>
    <div class="btn-group">
        <c:forEach items="${generalOperations}" var="o">
            <c:if test="${empty owner}">
                <jpm:operation-link operation="${o}" clazz="btn" contextualEntity="${contextualEntity}" instanceId="${item.id}" entityName="${entityName}" />
            </c:if>
            <c:if test="${not empty owner}">
                <jpm:operation-link operation="${o}" clazz="btn" contextualEntity="${owner.id}${entityContext}/${ownerId}/${contextualEntity}" instanceId="${item.id}" entityName="${entityName}" />
            </c:if>
        </c:forEach>
    </div>
</div>