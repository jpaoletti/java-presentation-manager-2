<%@tag description="Item del menu superior" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@attribute name = "code" required="true" type="java.lang.String" %>
<%@attribute name = "icon" required="false" type="java.lang.String" %>
<%@attribute name = "separator" required="false" type="java.lang.Boolean" %>
<security:authorize access="hasAuthority('jpm.auth.operation.${code}.list')">
    <c:if test="${separator}"><li class="separator" /></c:if>
    <li id="menu-${code}"><a href="${cp}jpm/${code}/list"><span class="${icon}"></span> <spring:message code="top.menu.${code}" text="${code}" /></a></li>
</security:authorize>