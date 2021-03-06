<div class="btn-group">
    <c:if test="${not operation.compact}">
        <c:forEach items="${itemOperations}" var="o">
            <jpm:operation-link operation="${o}" clazz="btn btn-sm ${not empty o.color?o.color:'btn-secondary'}" contextualEntity="${contextualEntity}" instanceId="${instance.id}" entityName="${entityName}" title="true" />
        </c:forEach>
    </c:if>
    <c:if test="${operation.compact}">
        <div class="dropdown">
            <button type="button" class="btn btn-sm btn-secondary dropdown-toggle" data-bs-toggle="dropdown">
                <span class="fas fa-cog"></span> <span class="caret"></span>
            </button>
            <ul class="dropdown-menu" aria-labelledby="dropdownMenuButton">
                <c:forEach items="${itemOperations}" var="o">
                    <li><jpm:operation-link operation="${o}" clazz="btn btn-sm dropdown-item ${not empty o.color?o.color:'btn-secondary'}" contextualEntity="${contextualEntity}" instanceId="${instance.id}" entityName="${entityName}" title="true" /></li>
                </c:forEach>
            </ul>
        </div>
    </c:if>
</div>