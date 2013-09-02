<!-- fixed inline style to avoid less -->
<div id="loading-div" style="position:absolute;bottom:0;left:0;right:0;top:0;background-color:#000;color:#fff;z-index:9999;">
    <div class="mainLoading" >
        <img src="${cp}static/img/main_loading.gif"  alt="..." style="width: 150px; height: 150px; position: fixed;top: 50%; left: 50%;margin-top: -64px; margin-left: -64px;" />
    </div>
</div>
<div id="header">
    <h1><a href="${cp}index">${jpm.title}</a></h1>
    <a id="menu-trigger" href="#"><i class="glyphicon glyphicon-align-justify"></i></a>
</div>
<div id="user-nav">
    <ul class="btn-group">
        <li class="btn"><a title="" href="#"><i class="glyphicon glyphicon-cog"></i> <span class="text"><spring:message code="jpm.login.profile" text="Logout" /></span></a></li>
        <li class="btn"><a title="" href="${cp}j_spring_security_logout"><i class="glyphicon glyphicon-share-alt"></i> <span class="text"><spring:message code="jpm.login.logout" text="Logout" /></span></a></li>
    </ul>
</div>