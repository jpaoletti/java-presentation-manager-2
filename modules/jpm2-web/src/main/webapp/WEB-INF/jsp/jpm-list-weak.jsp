<%@include file="inc/default-taglibs.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<table class="table table-bordered table-compact table-striped table-weak-list">
    <thead>
        <tr>
            <c:forEach items="${paginatedList.fields}" var="field">
                <c:if test="${showOperations}"><th></th></c:if>
                <th data-field="${field.id}">
                    <jpm:field-title entity="${entity}" fieldId="${field.id}" />
                </th>
            </c:forEach>
        </tr>
    </thead>
    <tbody>
        <c:forEach items="${paginatedList.contents}" var="item">
            <tr data-id="${item.id}">
                <c:if test="${showOperations}">
                    <td>
                        <c:forEach items="${item.operations}" var="o">
                            <a
                                class="btn btn-mini btn-default confirm-${o.confirm}" 
                                title="<spring:message code="${o.title}" text="${o.title}" arguments="${entityName}" />"
                                href="${cp}jpm/${entity.id}/${item.id}/${o.id}">
                                <i class="glyphicon jpmicon-${o.id}"></i>
                            </a>
                        </c:forEach>
                    </td>
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