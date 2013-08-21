<div id="sidebar">
    <ul>
        <li id="menu-home"><a href="index"><i class="glyphicon glyphicon-home"></i> <span><spring:message code="jpm.index.home" text="Home" /></span></a></li>
        <security:authorize ifAnyGranted="ROLE_DEVELOPER">
            <li id="menu-status"><a href="jpmStatus"><i class="glyphicon glyphicon-cog"></i> <span><spring:message code="jpm.menu.status" text="Status" /></span></a></li>
        </security:authorize>
        <li class="submenu">
            <a href="#"><i class="glyphicon glyphicon-th-list"></i> <span><spring:message code="jpm.menu.entities" text="Entities" /></span></a>
            <ul>
                <li><a href="list?entityId=jpm-entity-test"><spring:message code="jpm.entity.title.test" text="Test Entity" /></a></li>
            </ul>
        </li>
    </ul>
</div>