<div id="content-header">
    <h1>${operationName}</h1>
    <div class="btn-group">
        <c:forEach items="${generalOperations}" var="o">
            <a href="<c:url value='/jpm/${entity.id}/${o.id}' />" class="btn confirm-${o.confirm}" title="<spring:message code='${o.title}' text='${o.title}' arguments='${entityName}' />">
                <span class="glyphicon jpmicon-${o.id}"></span>
            </a>
        </c:forEach>
    </div>
</div>