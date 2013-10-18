<%@include file="loading.jsp" %>
<div id="header">
    <h1><a href="${cp}index">${jpm.title}</a></h1>
    <a id="menu-trigger" href="#"><i class="glyphicon glyphicon-align-justify"></i></a>
</div>
<div id="user-nav">
    <ul class="btn-group">
        <li class="btn" id="userNavRecent">
            <a href="#" data-toggle="dropdown" data-target="#userNavRecent" class="dropdown-toggle"><i class="glyphicon glyphicon-time"></i> <span class="text"><spring:message code="jpm.usernav.recent" text="Recent" /></span> <b class="caret"></b></a>
            <ul class="dropdown-menu"></ul>
        </li>
        <li class="btn"><a title="" href="${cp}jpm/user/${user.username}/profile"><i class="glyphicon glyphicon-cog"></i> <span class="text"><spring:message code="jpm.login.profile" text="Logout" /></span></a></li>
        <li class="btn"><a title="" href="${cp}j_spring_security_logout"><i class="glyphicon glyphicon-share-alt"></i> <span class="text"><spring:message code="jpm.login.logout" text="Logout" /></span></a></li>
    </ul>
</div>