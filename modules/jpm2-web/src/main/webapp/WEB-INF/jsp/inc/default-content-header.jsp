<div id="content-header">
    <h1>${operationName}</h1>
    <div class="btn-group">
        <c:forEach items="${generalOperations}" var="o">
            <c:if test="${empty owner}">
                <a href="${cp}jpm/${entity.id}/${o.id}" id="general-operation-${o.id}" class="btn confirm-${o.confirm}" title="<spring:message code='${o.title}' text='${o.title}' arguments='${entityName}' />">
                    <span class="glyphicon jpmicon-${o.id}"></span>
                </a>
            </c:if>
            <c:if test="${not empty owner}">
                <a href="${cp}jpm/${owner.id}/${ownerId}/${entity.id}/${o.id}" id="general-operation-${o.id}" class="btn confirm-${o.confirm}" title="<spring:message code='${o.title}' text='${o.title}' arguments='${entityName}' />">
                    <span class="glyphicon jpmicon-${o.id}"></span>
                </a>
            </c:if>
        </c:forEach>
    </div>
</div>