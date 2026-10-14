<%@tag description="Dynamic top menu (jpm_menu), already filtered by JpmMenuService" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="jpm" %>
<%@attribute name = "items" required="true" type="java.util.List" %>
<%@attribute name = "level" required="false" type="java.lang.Integer" %>
<c:set var="lvl" value="${empty level ? 0 : level}" />
<c:forEach items="${items}" var="node">
    <c:set var="nodeIcon"><c:if test="${not empty node.icon}"><i class="${fn:escapeXml(node.icon)}"></i>&nbsp;</c:if></c:set>
    <c:set var="nodeHref" value="${node.absolute ? '' : cp}${node.href}" />
    <c:choose>
        <c:when test="${lvl == 0 and node.group}">
            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" role="button" data-bs-toggle="dropdown" aria-expanded="false">${nodeIcon}<span class="menu-text">${node.labelHtml}</span></a>
                <ul class="dropdown-menu">
                    <jpm:dynamic-menu items="${node.children}" level="${lvl + 1}" />
                </ul>
            </li>
        </c:when>
        <c:when test="${lvl == 0 and node.link}">
            <li class="nav-item"><a class="nav-link jpm-menu-item" id="${fn:escapeXml(node.htmlId)}" href="${fn:escapeXml(nodeHref)}">${nodeIcon}<span class="menu-text">${node.labelHtml}</span></a></li>
        </c:when>
        <c:when test="${node.group}">
            <li><a class="dropdown-item" href="#">${nodeIcon}${node.labelHtml} &raquo;</a>
                <ul class="submenu dropdown-menu">
                    <jpm:dynamic-menu items="${node.children}" level="${lvl + 1}" />
                </ul>
            </li>
        </c:when>
        <c:when test="${node.link}">
            <li><a href="${fn:escapeXml(nodeHref)}" id="${fn:escapeXml(node.htmlId)}" class="jpm-menu-item dropdown-item">${nodeIcon}${node.labelHtml}</a></li>
        </c:when>
        <c:when test="${node.separator}">
            <li><hr class="dropdown-divider"></li>
        </c:when>
        <c:when test="${node.header}">
            <li><h6 class="dropdown-header">${nodeIcon}${node.labelHtml}</h6></li>
        </c:when>
    </c:choose>
</c:forEach>
