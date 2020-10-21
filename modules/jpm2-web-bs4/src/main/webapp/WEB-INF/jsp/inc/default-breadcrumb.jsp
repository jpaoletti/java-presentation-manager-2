<nav aria-label="breadcrumb" id="breadcrumb">
    <ol class="breadcrumb">
        <li class="breadcrumb-item">
            <a href="${cp}index" title="" class="tip-bottom"><span class="fas fa-home"></span> <span class="d-none d-md-inline"><spring:message code="jpm.index.home" text="Home" /></span></a>
        </li>
        <c:if test="${not empty contextualEntity}">
            <c:if test="${not empty owner}">
                <c:if test="${owner.containingListOperation}">
                    <c:if test="${empty instance.owner.owner}">
                        <li class="breadcrumb-item">
                            <a href="${cp}jpm/${owner.id}${entityContext}/list">
                                <span class="fas fa-th-list"></span> <span class="d-none d-md-inline"><spring:message code="jpm.operation.list" text="List" arguments="${owner.plularTitle}" /></span>
                            </a>
                        </li>
                    </c:if>
                    <c:if test="${not empty instance.owner.owner}">
                        <li class="breadcrumb-item">
                            <a href="${cp}jpm/${instance.owner.owner.entity.id}/${instance.owner.owner.iobject.id}/${owner.id}${entityContext}/list">
                                <span class="fas fa-th-list"></span> <span class="d-none d-md-inline"><spring:message code="jpm.operation.list" text="List" arguments="${owner.plularTitle}" /></span>
                            </a>
                        </li>
                    </c:if>
                </c:if>
                <security:authorize access="hasRole('jpm.auth.operation.${owner.id}.show')">
                    <li class="breadcrumb-item">
                        <a href="${cp}jpm/${owner.id}${entityContext}/${ownerId}/show.exec">
                            <span class="fas fa-search"></span> <span class="d-none d-md-inline"><spring:message code="jpm.operation.show" text="Show" arguments="${owner.title}" /> </span>
                            ${instance.owner.iobject.object}
                        </a>
                    </li>
                </security:authorize>
            </c:if>
            <c:if test="${operation.id ne 'list' and entity.containingListOperation}">
                <li class="breadcrumb-item">
                    <c:if test="${not empty owner}">
                        <!-- BUG NEED OWNER CONTEXT --> 
                        <a href="${cp}jpm/${owner.id}${entityContext}/${ownerId}/${contextualEntity}/list">
                            <span class="fas fa-th-list"></span> <span class="d-none d-md-inline">${entity.pluralTitle}</span>
                        </a>
                    </c:if>
                    <c:if test="${empty owner}">
                        <a href="${cp}jpm/${contextualEntity}/list">
                            <span class="fas fa-th-list"></span> <span class="d-none d-md-inline">${entity.pluralTitle}</span>
                        </a>
                    </c:if>
                </li>
            </c:if>
            <li class="breadcrumb-item active" aria-current="page">
                <span class="${operation.icon}"></span> <span class="d-none d-md-inline">${operationName} ${instance.iobject.object}</span>
            </li>
        </c:if>
    </ol>
</nav>
<div class="asynchronicProgress hide" id="asynchronicProgress">
    <div class="progress-bar bg-success" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" ></div><br/>
    <div id="asynchronicProgress_status" class="asynchronicProgressStatus"></div>
</div>
