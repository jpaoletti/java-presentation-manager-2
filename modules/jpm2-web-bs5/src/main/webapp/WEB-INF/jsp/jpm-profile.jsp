<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html lang="${locale.language}">
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <c:set var="entityName" value="${entity.title}" />
    <spring:message var="operationName" code="${operation.title}" arguments="${entityName}" text="Profile" />
    <jpm:jpm-body>
        <%@include file="inc/default-itemop-header.jsp" %>
        <div class="container-fluid">
            <div class="row"><br/>
                <div class="col-lg-12">
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
                                            <div class="control-group mb-2">
                                                <label class="control-label col-form-label" for="input01"><spring:message text="Username" code="jpm.profile.username" /></label>
                                                <div class="controls">
                                                    <input type="text" class="form-control" value="${user.username}" disabled="disabled"/>
                                                </div>
                                            </div>
                                            <div class="control-group mb-2">
                                                <label class="control-label col-form-label" for="name"><spring:message text="?" code="profile.edit.name" /></label>
                                                <div class="controls">
                                                    <input type="text" class="form-control" name="nombre" value="${user.name}" />
                                                </div>
                                            </div>
                                            <div class="control-group mb-2">
                                                <label class="control-label col-form-label" for="email"><spring:message text="?" code="profile.edit.mail" /></label>
                                                <div class="controls">
                                                    <input type="text" class="form-control" name="mail" value="${user.mail}" />
                                                </div>
                                            </div><br/>
                                            <button type="submit" class="btn btn-success"><spring:message code="jpm.form.submit" text="Submit" /></button>
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
                                        <div class="control-group mb-2">
                                            <label class="control-label col-form-label"><spring:message text="current Password" code="jpm.profile.chpass.current" /></label>
                                            <div class="controls">
                                                <input class="form-control" type="password" name="current" value="">
                                            </div>
                                        </div>
                                        <spring:message var="pwdShow" code="jpm.converter.password_converter.show" />
                                        <spring:message var="pwdMask" code="jpm.converter.password_converter.mask" />
                                        <spring:message var="pwdGenerate" code="jpm.converter.password_converter.generate" />
                                        <spring:message var="pwdWeak" code="jpm.converter.password_converter.weak" />
                                        <spring:message var="pwdMedium" code="jpm.converter.password_converter.medium" />
                                        <spring:message var="pwdGood" code="jpm.converter.password_converter.good" />
                                        <div id="d_newpass" class="control-group mb-2">
                                            <label class="control-label col-form-label"><spring:message text="New Password" code="jpm.profile.chpass.newpass" /></label>
                                            <div class="controls">
                                                <div class="input-group">
                                                    <input autocomplete="off" type="password" class="form-control"
                                                           name="newpass" id="f_newpass" value="" />
                                                    <button type="button" class="btn btn-outline-secondary"
                                                            id="f_newpass_toggle"
                                                            data-show-text="${pwdShow}" data-mask-text="${pwdMask}">
                                                        <i class="fas fa-eye"></i> <span class="pwd-toggle-text">${pwdShow}</span>
                                                    </button>
                                                    <button type="button" class="btn btn-outline-secondary"
                                                            id="f_newpass_generate">
                                                        <i class="fas fa-key"></i> ${pwdGenerate}
                                                    </button>
                                                </div>
                                                <div class="progress mt-1" style="height: 4px;">
                                                    <div id="f_newpass_strength" class="progress-bar" role="progressbar"
                                                         style="width: 0%;" aria-valuemin="0" aria-valuemax="100"></div>
                                                </div>
                                                <small id="f_newpass_strength_text" class="form-text text-muted"></small>
                                            </div>
                                        </div><br/><br/>
                                        <button type="submit" class="btn btn-success"><spring:message code="jpm.form.submit" text="Submit" /></button>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <script type="text/javascript" src="${cp}static/js/pwd-converter.js"></script>
        <script type="text/javascript">
            jpmLoad(function () {
                buildAjaxJpmForm();
                JpmPwdConverter.init({
                    inputId: 'f_newpass',
                    toggleId: 'f_newpass_toggle',
                    generateId: 'f_newpass_generate',
                    strengthBarId: 'f_newpass_strength',
                    strengthTextId: 'f_newpass_strength_text',
                    disableFormAutocomplete: false,
                    labels: {
                        weak: "${pwdWeak}",
                        medium: "${pwdMedium}",
                        good: "${pwdGood}"
                    }
                });
            });
        </script>
    </jpm:jpm-body>
</html>
