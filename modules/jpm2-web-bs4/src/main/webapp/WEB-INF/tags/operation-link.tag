<%@tag description="Title for field" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@attribute name = "operation"         required="true"  type="jpaoletti.jpm2.core.model.Operation" %>
<%@attribute name = "clazz"             required="false" type="java.lang.String" %>
<%@attribute name = "entityName"        required="false" type="java.lang.String" %>
<%@attribute name = "instanceId"        required="false" type="java.lang.String" %>
<%@attribute name = "contextualEntity"  required="true"  type="java.lang.String" %>
<%@attribute name = "title"             required="false" type="java.lang.Boolean" description="Show title " %>
<a
    id="operation-${operation.id}"
    class="${clazz} confirm-${operation.confirm} ${operation.synchronic?'synchronic':'asynchronic'}" 
    title="<spring:message code="${operation.title}" text="${operation.title}" arguments="${entityName}" />"
    href="${cp}jpm/${contextualEntity}/${not empty instanceId?instanceId.concat('/'):''}${operation.pathId}">
    <span class="${operation.icon}"></span>
    <c:if test="${not empty title and title}">
        <span class="d-none d-sm-none d-lg-inline">
            <spring:message code="${operation.title}" text="${operation.title}" arguments="${entityName}" />
        </span>
    </c:if>
</a>