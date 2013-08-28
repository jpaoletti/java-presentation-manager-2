<div id="header">
    <h1><a href="/index">${jpm.title}</a></h1>	
    <a id="menu-trigger" href="#"><i class="glyphicon glyphicon-align-justify"></i></a>	
</div>
<div id="user-nav">
    <ul class="btn-group">
        <!--<li class="btn" ><a title="" href="#"><i class="glyphicon glyphicon-user"></i> <span class="text">Profile</span></a></li>-->
<!--        <li class="btn dropdown" id="menu-messages"><a href="#" data-toggle="dropdown" data-target="#menu-messages" class="dropdown-toggle"><i class="glyphicon glyphicon-envelope"></i> <span class="text">Messages</span> <span class="label label-danger">5</span> <b class="caret"></b></a>
            <ul class="dropdown-menu">
                <li><a class="sAdd" title="" href="#">new message</a></li>
                <li><a class="sInbox" title="" href="#">inbox</a></li>
                <li><a class="sOutbox" title="" href="#">outbox</a></li>
                <li><a class="sTrash" title="" href="#">trash</a></li>
            </ul>
        </li>-->
        <li class="btn"><a title="" href="#"><i class="glyphicon glyphicon-cog"></i> <span class="text"><spring:message code="jpm.login.profile" text="Logout" /></span></a></li>
        <li class="btn"><a title="" href="j_spring_security_logout"><i class="glyphicon glyphicon-share-alt"></i> <span class="text"><spring:message code="jpm.login.logout" text="Logout" /></span></a></li>
    </ul>
</div>