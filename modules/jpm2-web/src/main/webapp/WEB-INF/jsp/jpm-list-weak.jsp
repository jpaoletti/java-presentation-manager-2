<%@include file="inc/default-taglibs.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<table>
    <thead>
        <tr>
            <c:forEach items="${paginatedList.fields}" var="field">
                <th data-field="${field.id}">
                    <jpm:field-title entity="${entity}" fieldId="${field.id}" />
                </th>
            </c:forEach>
        </tr>
    </thead>
    <tbody>
        <c:forEach items="${paginatedList.contents}" var="item">
            <tr data-id="${item.id}">
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