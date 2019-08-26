<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<table class="table table-compact table-bordered">
    <thead>
        <tr>
            <c:if test="${empty reportData.groups and empty reportData.formulas and not empty report.descriptiveFieldList and report.allowDetail}">
                <c:forEach var="f" items="${reportData.visibleFields}">
                    <c:if test="${not empty f}">
                    <th><jpm:field-title entity="${report.entity}" fieldId="${f}" /></th>
                    </c:if>
                    </c:forEach>
                </c:if>
                <c:if test="${not empty reportData.groups or not empty reportData.formulas or empty report.descriptiveFieldList or not report.allowDetail}">
                    <c:forEach var="f" items="${reportData.groups}">
                    <c:if test="${not empty f}">
                    <th><jpm:field-title entity="${report.entity}" fieldId="${f}" /></th>
                    </c:if>
                    </c:forEach>
                    <c:forEach var="f" items="${reportData.formulas}">
                    <th>${f.formula}(<jpm:field-title entity="${report.entity}" fieldId="${f.field}" />)</th>
                    </c:forEach>
                <th style="width: 120px;"><spring:message code="jpm.reports.list.count" text="Count" /></th>
                </c:if>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="row" items="${result.mainData}">
            <tr>
                <c:forEach var="item" items="${row}" varStatus="st">
                    <td class="report-value field ${((not empty reportData.groups || not empty reportData.formulas) && st.last)?'report-count-value':''}">
                        <c:if test="${fn:startsWith(item, '@page:')}">
                            <jsp:include page="converter/${fn:replace(item, '@page:', '')}" flush="true" />
                        </c:if>
                        <c:if test="${not fn:startsWith(item, '@page:')}">
                            ${item}
                        </c:if>
                    </td>
                </c:forEach>
            </tr>
        </c:forEach>
    </tbody>
</table>