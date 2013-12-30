<%@include file="loading.jsp" %>
<div class="navbar navbar-default navbar-fixed-top" role="navigation">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="${cp}index">${jpm.title}</a>
        </div>
        <div class="collapse navbar-collapse">
            <%@include file="menu/top-menu.jsp" %>
            <ul class="nav navbar-nav navbar-right">
                <li id="userNavRecent">
                    <a href="#" data-toggle="dropdown" data-target="#userNavRecent" class="dropdown-toggle"><i class="glyphicon glyphicon-time"></i> <span class="text"><spring:message code="jpm.usernav.recent" text="Recent" /></span> <b class="caret"></b></a>
                    <ul class="dropdown-menu"></ul>
                </li>
                <li><a title="" href="${cp}jpm/user/${user.username}/profile"><i class="glyphicon glyphicon-cog"></i> <span class="text"><spring:message code="jpm.login.profile" text="Logout" /></span></a></li>
                <li><a title="" href="${cp}j_spring_security_logout"><i class="glyphicon glyphicon-share-alt"></i> <span class="text"><spring:message code="jpm.login.logout" text="Logout" /></span></a></li>
            </ul>
        </div>
    </div>
</div>