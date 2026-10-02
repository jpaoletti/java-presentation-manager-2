<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html lang="${locale.language}">
    <head><%@include file="inc/default-head.jsp" %></head>
    <jpm:jpm-body>
        <jpm:jpm-item-operation>
            <div id="jpm-selection-review" data-confirm="${operation.confirm}">
                <p><strong><span class="jpm-selection-count">${selectionRows.size()}</span> <spring:message code="jpm.selection.selected" /></strong></p>
                <p><spring:message code="jpm.selection.reviewHelp" /></p>
                <div class="table-responsive">
                    <table class="table table-bordered table-sm">
                        <thead class="table-secondary">
                            <tr>
                                <th>ID</th>
                                <c:forEach items="${selectionFields}" var="field">
                                    <th><jpm:field-title entity="${entity}" fieldId="${field.id}" /></th>
                                </c:forEach>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${selectionRows}" var="item">
                                <tr data-selection-id="${fn:escapeXml(item.id)}" data-unavailable="${item.unavailable}">
                                    <td><c:out value="${item.id}" /></td>
                                    <c:if test="${item.unavailable}">
                                        <td colspan="${empty selectionFields ? 1 : selectionFields.size()}" class="text-danger"><spring:message code="jpm.selection.unavailable" /></td>
                                    </c:if>
                                    <c:if test="${not item.unavailable}">
                                        <c:forEach items="${selectionFields}" var="f">
                                            <td>
                                                <c:set var="convertedValue" value="${item['values'][f.id]}" />
                                                <c:set var="field" value="${f.id}" scope="request" />
                                                <c:if test="${fn:startsWith(convertedValue, '@page:')}">
                                                    <jsp:include page="converter/${fn:replace(convertedValue, '@page:', '')}" flush="true" />
                                                </c:if>
                                                <c:if test="${not fn:startsWith(convertedValue, '@page:')}">${convertedValue}</c:if>
                                            </td>
                                        </c:forEach>
                                    </c:if>
                                    <td><button type="button" class="btn btn-sm btn-outline-danger jpm-selection-remove"><spring:message code="jpm.selection.remove" /></button></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                <button type="button" class="btn btn-primary jpm-selection-continue"><spring:message code="${operation.confirm ? 'jpm.selection.confirm' : 'jpm.selection.continue'}" /></button>
                <button type="button" class="btn btn-secondary jpm-selection-cancel"><spring:message code="jpm.modal.confirm.cancel" /></button>
            </div>
        </jpm:jpm-item-operation>
    </jpm:jpm-body>
</html>
