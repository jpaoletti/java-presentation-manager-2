<div id="breadcrumb">
    <a href="${cp}${currentHome}" title="" class="tip-bottom"><i class="glyphicon glyphicon-home"></i> <spring:message code="jpm.index.home" text="Home" /></a>
    <c:if test="${not empty owner}">
        <a href="${cp}jpm/${owner.id}/${ownerId}/show">
            <i class="glyphicon glyphicon-search"></i> <spring:message code="jpm.operation.show" text="Show" arguments="${owner.title}" />
        </a>
    </c:if>
    <a href="#" class="current"><i class="glyphicon jpmicon-${operation.id}"></i>${operationName}</a>
</div>