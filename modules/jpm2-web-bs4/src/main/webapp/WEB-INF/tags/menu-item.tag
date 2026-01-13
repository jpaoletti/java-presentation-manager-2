<%@tag description="Top menu item" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@attribute name = "code" required="true" type="java.lang.String" %>
<%@attribute name = "icon" required="false" type="java.lang.String" %>
<%@attribute name = "separator" required="false" type="java.lang.Boolean" %>
<security:authorize access="hasAuthority('jpm.auth.operation.${code}.list')">
    <c:choose>
        <c:when test = "${jpm.menuMode == 'top'}">
            <a href="${cp}jpm/${code}/list" id="menu-${code}" class="jpm-menu-item  dropdown-item"><span class="${icon}"></span>&nbsp;<spring:message code="top.menu.${code}" text="${code}" /></a>
        </c:when>
        <c:when test = "${jpm.menuMode == 'left'}">
            <li><a href="${cp}jpm/${code}/list" id="menu-${code}" class="jpm-menu-item"><span class="${icon}"></span>&nbsp;<spring:message code="top.menu.${code}" text="${code}" /></a></li>
        </c:when>
    </c:choose>
    <c:if test="separator">
        <div class="dropdown-divider"></div>
    </c:if>
</security:authorize>