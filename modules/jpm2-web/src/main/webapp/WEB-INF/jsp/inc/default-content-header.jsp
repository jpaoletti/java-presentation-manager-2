<div id="content-header">
    <h1>${operationName}</h1>
    <div class="btn-group">
        <s:iterator value="generalOperations" var="o">
            <a href="${o.id}?entityId=${entity.id}" class="btn confirm-${o.confirm}" title="<spring:message code='${o.title}' text='${o.title}' arguments='${entityName}' />">
                <i class="glyphicon jpmicon-${o.id}"></i>
            </a>
        </s:iterator>
    </div>
</div>