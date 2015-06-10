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
                <div class="col-12">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <%@include file="inc/item-operations.jsp" %>
                        </div>
                        <div class="panel-body">
                            <div class="row jpm-content-panels">
                                <div class="col-sm-6">
                                    <div class="panel panel-default">
                                        <div class="panel-heading">
                                            <h3 class="panel-title">
                                                <span class="glyphicon glyphicon-info-sign"></span> &nbsp;
                                                <spring:message code="jpm.profile.info" text="User Information" />
                                            </h3>
                                        </div>
                                        <div class="panel-body">
                                            <form class="form-horizontal" role="form" action="#">
                                                <fieldset>
                                                    <%-- <img src="${user.gravatar}?d=mm&s=100" alt="gravatar" /><br/>--%>
                                                    <div class="control-group">
                                                        <label class="control-label" for="input01"><spring:message text="Username" code="jpm.profile.username" /></label>
                                                        <div class="controls">
                                                            <input type="text" class="form-control" value="${user.username}" disabled="disabled"/>
                                                        </div>
                                                    </div>
                                                    <%-- <div class="control-group">
                                                        <label class="control-label" for="name"><spring:message text="?" code="profile.edit.name" /></label>
                                                        <div class="controls">
                                                            <input type="text" name="name" value="${user.name}" />
                                                        </div>
                                                    </div>
                                                    <div class="control-group">
                                                        <label class="control-label" for="email"><spring:message text="?" code="profile.edit.mail"/></label>
                                                        <div class="controls">
                                                            <input type="text" name="email" value="${user.email}" placeholder="<spring:message text="?" code="profile.edit.mail"/>" />
                                                        </div>
                                                    </div>--%>
                                                </fieldset>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-sm-6">
                                    <div class="panel panel-default">
                                        <div class="panel-heading">
                                            <h3 class="panel-title">
                                                <span class="glyphicon glyphicon-lock"></span> &nbsp;
                                                <spring:message code="jpm.profile.chpass" text="Change Password" />
                                            </h3>
                                        </div>
                                        <div class="panel-body">
                                            <form class="form-horizontal" role="form" action="" method="POST">
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
        <script type="text/javascript" src="${cp}static/js/pwdwidget.js?v=${jpm.appversion}"></script>
        <script type="text/javascript">
            jpmLoad(function() {
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