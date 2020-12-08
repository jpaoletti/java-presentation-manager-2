<ol class="breadcrumb" id="breadcrumb">
    <li>
        <a href="${cp}${currentHome}" title="" class="tip-bottom"><span class="glyphicon glyphicon-home"></span> <span class="hidden-xs"><spring:message code="jpm.index.home" text="Home" /></span></a>
    </li>
    <c:if test="${not empty owner}">
        <c:if test="${owner.containingListOperation}">
            <security:authorize access="hasRole('jpm.auth.operation.${owner.id}.list')">
                <c:if test="${empty instance.owner.owner}">
                    <li>
                        <a href="${cp}jpm/${owner.id}${entityContext}/list">
                            <span class="glyphicon jpmicon-list"></span> <span class="hidden-xs"><spring:message code="jpm.operation.list" text="List" arguments="${owner.title}" /></span>
                        </a>
                    </li>
                </c:if>
                <c:if test="${not empty instance.owner.owner}">
                    <li>
                        <a href="${cp}jpm/${instance.owner.owner.entity.id}/${instance.owner.owner.iobject.id}/${owner.id}${entityContext}/list">
                            <span class="glyphicon jpmicon-list"></span> <span class="hidden-xs"><spring:message code="jpm.operation.list" text="List" arguments="${owner.title}" /></span>
                        </a>
                    </li>
                </c:if>
            </security:authorize>
        </c:if>
        <security:authorize access="hasRole('jpm.auth.operation.${owner.id}.show')">
            <li>
                <a href="${cp}jpm/${owner.id}${entityContext}/${ownerId}/show.exec">
                    <span class="glyphicon jpmicon-show"></span> <span class="hidden-xs"><spring:message code="jpm.operation.show" text="Show" arguments="${owner.title}" /> </span>
                    ${instance.owner.iobject.object}
                </a>
            </li>
        </security:authorize>
    </c:if>
    <c:if test="${operation.id ne 'list' and entity.containingListOperation}">
        <li>
        <c:if test="${not empty owner}">
            <!-- BUG NEED OWNER CONTEXT --> 
            <a href="${cp}jpm/${owner.id}${entityContext}/${ownerId}/${contextualEntity}/list">
                <span class="glyphicon jpmicon-list"></span> <span class="hidden-xs"><spring:message code="jpm.operation.list" text="List" arguments="${entity.title}" /></span>
            </a>
        </c:if>
        <c:if test="${empty owner}">
            <a href="${cp}jpm/${contextualEntity}/list">
                <span class="glyphicon jpmicon-list"></span> <span class="hidden-xs"><spring:message code="jpm.operation.list" text="List" arguments="${entity.title}" /></span>
            </a>
        </c:if>
        </li>
    </c:if>
    <li class="active">
        <a href="javascript: void(0)"><span class="glyphicon jpmicon-${operation.id}"></span> <span class="hidden-xs">${operationName} ${instance.iobject.object}</span></a>
    </li>
</ol>
<div class="asynchronicProgress hide" id="asynchronicProgress">
    <div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" ></div><br/>
    <div id="asynchronicProgress_status" class="asynchronicProgressStatus"></div>
</div>
