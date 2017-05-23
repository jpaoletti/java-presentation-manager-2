<div class="btn-group">
    <c:forEach items="${itemOperations}" var="o">
        <jpm:operation-link operation="${o}" clazz="btn btn-sm btn-default" contextualEntity="${contextualEntity}" instanceId="${instance.id}" entityName="${entityName}" title="true" />
    </c:forEach>
</div>