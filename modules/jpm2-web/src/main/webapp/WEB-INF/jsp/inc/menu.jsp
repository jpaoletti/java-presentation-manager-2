<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div id="sidebar">
    <ul>
        <li id="menu-home"><a href="index"><i class="glyphicon glyphicon-home"></i> <span><spring:message code="jpm.index.home" text="Home" /></span></a></li>
        <security:authorize ifAnyGranted="ROLE_DEVELOPER">
            <li id="menu-status"><a href="jpmStatus"><i class="glyphicon glyphicon-cog"></i> <span><spring:message code="jpm.menu.status" text="Status" /></span></a></li>
        </security:authorize>
        <li class="submenu">
            <a href="#"><i class="glyphicon glyphicon-th-list"></i> <span><spring:message code="jpm.menu.entities" text="Entities" /></span></a>
            <ul>
                <li><a href="<c:url value="/jpm/jpm-entity-test/"/>"><spring:message code="jpm.entity.title.test" text="Test Entity" /></a></li>
            </ul>
        </li>
        <security:authorize ifAnyGranted="ROLE_USERADMIN">
            <li class="submenu">
                <a href="#"><i class="glyphicon glyphicon-user"></i> <span><spring:message code="jpm.menu.security" text="Security" /></span></a>
                <ul>
                    <li><a href="<c:url value="/jpm/jpm-entity-user/"/>"><spring:message code="jpm.entity.title.jpm-entity-user" text="Users" /></a></li>
                    <li><a href="<c:url value="/jpm/jpm-entity-group/"/>"><spring:message code="jpm.entity.title.jpm-entity-group" text="Groups" /></a></li>
                </ul>
            </li>
        </security:authorize>
    </ul>
</div>