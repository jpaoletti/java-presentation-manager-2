<%-- OVERRIDE ME --%>
<%@include file="../default-taglibs.jsp" %>
<ul>
    <li class="header-menu">
        <span>General</span>
    </li>
    <li class="sidebar-dropdown">
        <a href="#">
            <i class="fa fa-cogs"></i>
            <span class="menu-text">Configuration</span>
            <!--<span class="badge badge-pill badge-warning">New</span>-->
        </a>
        <div class="sidebar-submenu" style="display: none;">
            <ul>
                <li>
                    <jpm:menu-item code="jpm-entity-test"  icon="fab fa-java" />
                    <!--<span class="badge badge-pill badge-success">Pro</span>-->
                </li>
            </ul>
        </div>
    </li>
    

    <li class="header-menu">
        <span>Administration</span>
    </li>
    <security:authorize access="hasAnyRole('jpm.auth.operation.jpm-entity-user.list','jpm.auth.operation.jpm-entity-group.list')">
    <li class="sidebar-dropdown">
        <a href="#">
            <i class="fa fa-user"></i>
            <span class="menu-text"><spring:message code="jpm.menu.security" text="Security" /></span>
        </a>
        <div class="sidebar-submenu">
            <ul>
                <jpm:menu-item code="jpm-entity-user"  icon="fa fa-user"/>
                <jpm:menu-item code="jpm-entity-group"  icon="fa fa-users"/>
            </ul>
        </div>
    </li>
    </security:authorize>
</ul>