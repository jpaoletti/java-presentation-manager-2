<ol class="breadcrumb" id="breadcrumb">
    <li>
        <a href="${cp}${currentHome}" title="" class="tip-bottom"><span class="glyphicon glyphicon-home"></span> <spring:message code="jpm.index.home" text="Home" /></a>
    </li>
    <c:if test="${not empty owner}">
        <c:if test="${owner.containingListOperation}">
            <li>
            <a href="${cp}jpm/${owner.id}${entityContext}/list">
                <span class="glyphicon jpmicon-list"></span> <spring:message code="jpm.operation.list" text="List" arguments="${owner.title}" />
            </a>
            </li>
        </c:if>
        <li>
            <a href="${cp}jpm/${owner.id}${entityContext}/${ownerId}/show">
               <span class="glyphicon jpmicon-show"></span> <spring:message code="jpm.operation.show" text="Show" arguments="${owner.title}" /> 
               ${instance.owner.iobject.object}
            </a>
        </li>
    </c:if>
    <c:if test="${operation.id ne 'list' and entity.containingListOperation}">
        <li>
        <c:if test="${not empty owner}">
            <!-- BUG NEED OWNER CONTEXT --> 
            <a href="${cp}jpm/${owner.id}${entityContext}/${ownerId}/${contextualEntity}/list">
                <span class="glyphicon jpmicon-list"></span> <spring:message code="jpm.operation.list" text="List" arguments="${entity.title}" />
            </a>
        </c:if>
        <c:if test="${empty owner}">
            <a href="${cp}jpm/${contextualEntity}/list">
                <span class="glyphicon jpmicon-list"></span> <spring:message code="jpm.operation.list" text="List" arguments="${entity.title}" />
            </a>
        </c:if>
        </li>
    </c:if>
    <li class="active">
        <a href="#"><span class="glyphicon jpmicon-${operation.id}"></span> ${operationName} ${instance.iobject.object}</a> 
    </li>
</ol>
