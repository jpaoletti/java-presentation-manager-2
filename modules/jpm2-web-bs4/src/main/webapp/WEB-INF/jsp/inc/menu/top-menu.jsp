<security:authorize access="hasAnyRole('jpm.auth.operation.jpm-entity-user.list','jpm.auth.operation.jpm-entity-group.list')">
    <li class="nav-item dropdown">
        <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
            <span class="fa fa-user"></span> <spring:message code="jpm.menu.security" text="Security" />
        </a>
        <div class="dropdown-menu" aria-labelledby="navbarDropdown">
            <jpm:menu-item code="jpm-entity-user"  icon="fa fa-user"/>
            <jpm:menu-item code="jpm-entity-group"  icon="fa fa-users"/>
        </div>
    </li>
</security:authorize>