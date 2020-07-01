<%@tag description="Top menu item" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@attribute name = "code" required="true" type="java.lang.String" %>
<%@attribute name = "icon" required="false" type="java.lang.String" %>
<%@attribute name = "separator" required="false" type="java.lang.Boolean" %>
<security:authorize access="hasRole('jpm.auth.operation.${code}.list')">
    <li><a href="${cp}jpm/${code}/list" id="menu-${code}" class="jpm-menu-item"><i class="${icon}"></i>&nbsp;<spring:message code="top.menu.${code}" text="${code}" /></a></li>
</security:authorize>