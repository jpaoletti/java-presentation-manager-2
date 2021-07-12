<%-- OVERRIDE ME --%>
<%@include file="../default-taglibs.jsp" %>
<li class="nav-item dropdown">
    <a class="nav-link dropdown-toggle" role="button" data-bs-toggle="dropdown" aria-expanded="false"><i class="fa fa-cogs"></i><span class="menu-text"> Configuration</span></a>
    <ul class="dropdown-menu" aria-labelledby="dLabel">
        <jpm:menu-item code="jpm-entity-test"  icon="fab fa-java" />
    </ul>
</li>
<li class="nav-item dropdown">
    <a class="nav-link  dropdown-toggle"  role="button" data-bs-toggle="dropdown" aria-expanded="false"><i class="fa"></i><span class="menu-text">Administration</span></a>
    <ul class="dropdown-menu" aria-labelledby="dLabel">
        <security:authorize access="hasAnyRole('jpm.auth.operation.jpm-entity-user.list','jpm.auth.operation.jpm-entity-group.list')">
            <h6 class="dropdown-header"><i class="fa fa-user"></i> <spring:message code="jpm.menu.security" text="Security" /></h6>
            <jpm:menu-item code="jpm-entity-user"  icon="fa fa-user"/>
            <jpm:menu-item code="jpm-entity-group"  icon="fa fa-users"/>
        </security:authorize>
    </ul>
</li>