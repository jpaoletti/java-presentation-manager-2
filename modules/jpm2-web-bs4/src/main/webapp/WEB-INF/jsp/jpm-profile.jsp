<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html>
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <c:set var="entityName" value="${entity.title}" />
    <spring:message var="operationName" code="${operation.title}" arguments="${entityName}" text="Profile" />
    <jpm:jpm-body>
        <%@include file="inc/default-content-header.jsp" %>
        <%@include file="inc/default-breadcrumb.jsp" %>
        <div class="container-fluid">
            <div class="row"><br/>
                <div class="col-lg-12">
                    <div class="card">
                        <div class="card-header">
                            <%@include file="inc/item-operations.jsp" %>
                        </div>
                        <div class="card-body">
                            <div class="row jpm-content-panels">
                                <div class="col-sm-6">
                                    <div class="card">
                                        <div class="card-header">
                                            <h3 class="card-title">
                                                <span class="fas fa-info-circle"></span> &nbsp;
                                                <spring:message code="jpm.profile.info" text="User Information" />
                                            </h3>
                                        </div>
                                        <div class="card-body">
                                            <form class="form-horizontal" role="form" action="${cp}jpm/user/${user.username}/updateProfile">
                                                <fieldset>
                                                    <div class="control-group">
                                                        <label class="control-label" for="input01"><spring:message text="Username" code="jpm.profile.username" /></label>
                                                        <div class="controls">
                                                            <input type="text" class="form-control" value="${user.username}" disabled="disabled"/>
                                                        </div>
                                                    </div>
                                                    <div class="control-group">
                                                        <label class="control-label" for="name"><spring:message text="?" code="profile.edit.name" /></label>
                                                        <div class="controls">
                                                            <input type="text" class="form-control" name="nombre" value="${user.name}" />
                                                        </div>
                                                    </div>
                                                    <div class="control-group">
                                                        <label class="control-label" for="email"><spring:message text="?" code="profile.edit.mail" /></label>
                                                        <div class="controls">
                                                            <input type="text" class="form-control" name="mail" value="${user.mail}" />
                                                        </div>
                                                    </div><br/>
                                                    <button type="submit" class="btn btn-primary"><spring:message code="jpm.form.submit" text="Submit" /></button>
                                                </fieldset>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-sm-6">
                                    <div class="card">
                                        <div class="card-header">
                                            <h3 class="card-title">
                                                <span class="fas fa-lock"></span> &nbsp;
                                                <spring:message code="jpm.profile.chpass" text="Change Password" />
                                            </h3>
                                        </div>
                                        <div class="card-body">
                                            <form class="form-horizontal" role="form" action="" id="jpmForm" method="POST">
                                                <div class="control-group">
                                                    <label class="control-label"><spring:message text="current Password" code="jpm.profile.chpass.current" /></label>
                                                    <div class="controls">
                                                        <input class="form-control" type="password" name="current" value="">
                                                    </div>
                                                </div>
                                                <div id="d_newpass" class="control-group">
                                                    <label class="control-label"><spring:message text="New Password" code="jpm.profile.chpass.newpass" /></label>
                                                    <div class="controls">
                                                        <div class='pwdwidgetdiv' id='thepwddivnewpass'></div>
                                                    </div>
                                                </div><br/><br/>
                                                <button type="submit" class="btn btn-primary"><spring:message code="jpm.form.submit" text="Submit" /></button>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <script type="text/javascript" src="${cp}static/js/pwdwidget.js"></script>
        <script type="text/javascript" src="${cp}static/js/jquery.form.min.js?v=${jpm.appversion}"></script>
        <script type="text/javascript">
            jpmLoad(function () {
                buildAjaxJpmForm();
                var pwdwidget = new PasswordWidget('thepwddivnewpass', 'newpass');
                pwdwidget.txtShow = "<spring:message code='jpm.converter.password_converter.show' />";
                pwdwidget.txtMask = "<spring:message code='jpm.converter.password_converter.mask' />";
                pwdwidget.txtGenerate = "<spring:message code='jpm.converter.password_converter.generate' />";
                pwdwidget.txtWeak = "<spring:message code='jpm.converter.password_converter.weak' />";
                pwdwidget.txtMedium = "<spring:message code='jpm.converter.password_converter.medium' />";
                pwdwidget.txtGood = "<spring:message code='jpm.converter.password_converter.good' />";
                pwdwidget.MakePWDWidget();
            });
        </script>
    </jpm:jpm-body>
</html>