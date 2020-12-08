<%@include file="default-content-header.jsp" %>
<%@include file="default-breadcrumb.jsp" %>
<c:if test="${not empty itemOperations or not empty generalOperations}">
    <div class="row" id="item-operation-general-operations">
        <div class="col-md-6 col-sm-12">
            <%@include file="item-operations.jsp" %>
        </div>
        <div class="col-md-6 col-sm-12">
            <%@include file="general-operations.jsp" %>
        </div>
    </div>
</c:if>
<hr class="mb-3 mt-3"/>