<div class="btn-group">
    <s:iterator value="itemOperations" var="o">
        <a class="btn btn-sm btn-default" 
           href="${o.id}?entityId=${entity.id}&instanceId=${instance.id}">
            <i class="glyphicon jpmicon-${o.id}"></i> <spring:message code="${o.title}" text="${o.title}" arguments=" " />
        </a>
    </s:iterator>
</div>