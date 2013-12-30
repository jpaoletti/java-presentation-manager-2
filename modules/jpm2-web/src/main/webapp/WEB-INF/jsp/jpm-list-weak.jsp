<%@include file="inc/default-taglibs.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<table class="table table-bordered table-compact table-striped table-weak-list">
    <thead>
        <tr>
            <c:if test="${showOperations}"><th></th></c:if>
                <c:forEach items="${paginatedList.fields}" var="field">
                <th data-field="${field.id}">
                    <jpm:field-title entity="${entity}" fieldId="${field.id}" />
                </th>
            </c:forEach>
        </tr>
    </thead>
    <tbody>
        <spring:message var="entityName" code="jpm.entity.title.${weakId}" text=" " />
        <c:forEach items="${paginatedList.contents}" var="item">
            <tr data-id="${item.id}">
                <c:if test="${showOperations}">
                    <th class="operation-list" style="width: ${fn:length(item.operations) * 30 + 20}px">
            <div class="btn-group nowrap">
                <c:forEach items="${item.operations}" var="o">
                    <a
                        class="btn btn-xs btn-default confirm-${o.confirm} weak-operation" 
                        title="<spring:message code="${o.title}" text="${o.title}" arguments="${entityName}" />"
                        href="${cp}jpm/${entity.id}/${item.id}/${o.id}">
                        <i class="glyphicon jpmicon-${o.id}"></i>
                    </a>
                </c:forEach>
                </th>
            </c:if>
            <c:forEach items="${item.values}" var="v">
                <td data-field="${v.key}">
                    <c:set var="convertedValue" value="${v.value}"/>
                    <c:set var="field" value="${v.key}" scope="request" />
                    <c:if test="${fn:startsWith(convertedValue, '@page:')}">
                        <jsp:include page="converter/${fn:replace(convertedValue, '@page:', '')}" flush="true" />
                    </c:if>
                    <c:if test="${not fn:startsWith(convertedValue, '@page:')}">
                        ${convertedValue}
                    </c:if>
                </td>
            </c:forEach>
            </tr>
        </c:forEach>
        </tbody>
</table>
<script type="text/javascript">
    $(".inline-edit").each(function() {
        $(this).editable({ url: '${cp}jpm/${entity.id}/' + $(this).closest("tr").attr("data-id") + '/iledit', send: "always", emptytext: "-" });
    });
</script>