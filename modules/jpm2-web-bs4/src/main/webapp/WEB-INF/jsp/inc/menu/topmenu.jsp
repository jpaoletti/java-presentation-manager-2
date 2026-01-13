<%-- OVERRIDE ME --%>
<%@include file="../default-taglibs.jsp" %>
<li class="nav-item dropdown active">
    <a class="nav-link  dropdown-toggle" data-toggle="dropdown"  aria-haspopup="true" aria-expanded="false"><i class="fa fa-cogs"></i><span class="menu-text"> Configuration</span></a>
    <div class="dropdown-menu" aria-labelledby="dLabel">
        <jpm:menu-item code="jpm-entity-test"  icon="fab fa-java" />
    </div>
</li>
<li class="nav-item dropdown">
    <a class="nav-link  dropdown-toggle" data-toggle="dropdown"  aria-haspopup="true" aria-expanded="false"><i class="fa"></i><span class="menu-text">Administration</span></a>
    <div class="dropdown-menu" aria-labelledby="dLabel">
        <security:authorize access="hasAnyAuthority('jpm.auth.operation.jpm-entity-user.list','jpm.auth.operation.jpm-entity-group.list')">
            <h6 class="dropdown-header"><i class="fa fa-user"></i> <spring:message code="jpm.menu.security" text="Security" /></h6>
            <jpm:menu-item code="jpm-entity-user"  icon="fa fa-user"/>
            <jpm:menu-item code="jpm-entity-group"  icon="fa fa-users"/>
        </security:authorize>
    </div>
</li>