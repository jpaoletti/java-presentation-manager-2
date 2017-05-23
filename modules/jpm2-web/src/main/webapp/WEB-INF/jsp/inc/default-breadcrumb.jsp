<ol class="breadcrumb" id="breadcrumb">
    <li>
        <a href="${cp}${currentHome}" title="" class="tip-bottom"><span class="glyphicon glyphicon-home"></span> <spring:message code="jpm.index.home" text="Home" /></a>
    </li>
    <c:if test="${not empty owner}">
        <c:if test="${owner.containingListOperation}">
            <c:if test="${empty instance.owner.owner}">
                <li>
                    <a href="${cp}jpm/${owner.id}${entityContext}/list">
                        <span class="glyphicon jpmicon-list"></span> <spring:message code="jpm.operation.list" text="List" arguments="${owner.title}" />
                    </a>
                </li>
            </c:if>
            <c:if test="${not empty instance.owner.owner}">
                <li>
                    <a href="${cp}jpm/${instance.owner.owner.entity.id}/${instance.owner.owner.iobject.id}/${owner.id}${entityContext}/list">
                        <span class="glyphicon jpmicon-list"></span> <spring:message code="jpm.operation.list" text="List" arguments="${owner.title}" />
                    </a>
                </li>
            </c:if>
        </c:if>
        <li>
            <a href="${cp}jpm/${owner.id}${entityContext}/${ownerId}/show.exec">
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
<div class="asynchronicProgress hide" id="asynchronicProgress">
    <div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" ></div><br/>
    <div id="asynchronicProgress_status" class="asynchronicProgressStatus"></div>
</div>
