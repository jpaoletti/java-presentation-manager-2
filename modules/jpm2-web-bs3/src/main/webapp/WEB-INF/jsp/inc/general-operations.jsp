<div class="btn-group float-right">
    <c:forEach items="${generalOperations}" var="o">
        <c:if test="${empty owner}">
            <jpm:operation-link operation="${o}" clazz="btn" contextualEntity="${contextualEntity}" instanceId="${item.id}" entityName="${entityName}" />
        </c:if>
        <c:if test="${not empty owner}">
            <jpm:operation-link operation="${o}" clazz="btn" contextualEntity="${owner.id}${entityContext}/${ownerId}/${contextualEntity}" instanceId="${item.id}" entityName="${entityName}" />
        </c:if>
    </c:forEach>