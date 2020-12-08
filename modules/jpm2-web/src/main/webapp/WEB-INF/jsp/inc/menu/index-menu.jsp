<%@include file="../default-taglibs.jsp" %>
<ul class="nav bs-sidenav">
    <li id="menu-home"><a href="${cp}index"><i class="glyphicon glyphicon-home"></i> <span><spring:message code="jpm.index.home" text="Home" /></span></a></li>
        <security:authorize access="hasRole('ROLE_DEVELOPER')">
        <li id="menu-status"><a href="${cp}jpm"><i class="glyphicon glyphicon-cog"></i> <span><spring:message code="jpm.menu.status" text="Status" /></span></a></li>
        </security:authorize>
    <li id="menu-jpm-entity-test">
        <a href="${cp}jpm/jpm-entity-test/list"><i class="glyphicon glyphicon-th-list"></i><spring:message code="jpm.entity.title.test" text="Test Entity" /></a>
    </li>
    <li id="menu-testReport">
        <a href="${cp}jpm/report/testReport"><i class="glyphicon glyphicon-book"></i><spring:message code="jpm.entity.title.testReport" text="Test Entity Report" /></a>
    </li>
    <security:authorize access="hasRole('ROLE_USERADMIN')">
        <li id="menu-status"><a href="${cp}security"><i class="glyphicon glyphicon-user"></i> <span><spring:message code="jpm.menu.security" text="Security" /></span></a></li>
        <li class="submenu">
            <a href="javascript: void(0)"><i class="glyphicon glyphicon-user"></i> <span><spring:message code="jpm.menu.security2" text="Security (Dropdown)" /></span></a>
            <ul>
                <li><a href="${cp}jpm/jpm-entity-user/list"><spring:message code="jpm.entity.title.jpm-entity-user" text="Users" /></a></li>
                <li><a href="${cp}jpm/jpm-entity-group/list"><spring:message code="jpm.entity.title.jpm-entity-group" text="Groups" /></a></li>
            </ul>
        </li>
    </security:authorize>
</ul>