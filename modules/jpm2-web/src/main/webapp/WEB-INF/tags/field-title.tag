<%@tag description="Title for field" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@attribute name = "entity" required="true" type="jpaoletti.jpm2.core.model.Entity" %>
<%@attribute name = "fieldId" required="true" type="java.lang.String" %>
<%@attribute name = "showHelp" required="false" type="java.lang.Boolean" %>
<spring:message var="fieldTitle" code="jpm.field.${entity.id}.${fieldId}" text="${fieldId}" />
<spring:message var="fieldHelp" code="jpm.field.${entity.id}.${fieldId}.help" text="." />
<c:if test="${not (fieldHelp == '.')}">
    <c:if test="${(empty showHelp) or showHelp}">
        <a class='tip-top' data-toggle='tooltip' title='${fieldHelp}' href='#' >${fieldTitle}</a>
    </c:if>
    <c:if test="${(not empty showHelp) and not showHelp}">
        ${fieldTitle}
    </c:if>
</c:if>
<c:if test="${fieldHelp == '.'}">
    ${fieldTitle}
</c:if>