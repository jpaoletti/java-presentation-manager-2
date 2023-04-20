<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html lang="${locale.language}">
    <head>
        <%@include file="inc/default-head.jsp" %>
        <link href="${cp}static/css/signin.css?v=${jpm.appversion}" rel="stylesheet" type="text/css" />
        <link href="${cp}static/css/login.css?v=${jpm.appversion}" rel="stylesheet" type="text/css" />
        <style type="text/css">
            .input-group{padding: 0px;}
        </style>
    </head>
    <body id="login-body">
        <!--<main class="page-content pt-2">-->
        <div class="container-fluid p-0">
            <div class="row justify-content-center align-items-center">
                <div class="col-lg-3 col-sm-12">
                    <div class="card" style="max-width: 340px">
                        <div class="card-header">
                            <p class="h4 mb-0 font-weight-normal"><spring:message code="jpm.login.title" text="Enter username and password to continue." /></p>
                        </div>
                        <div class="card-body">
                            <form class="" id="loginform" action="login" method="POST">
                                <center>
                                    <img class="mb-4 mx-auto" src="<c:url value='/static/img/login.png'/>" alt="Login" id="loginLogo" style="width: 90%;">
                                </center>
                                <label for="j_username" class="sr-only"><spring:message code="jpm.login.username" text="Username" /></label>
                                <div class="input-group mb-1">
                                    <div class="input-group-text">
                                        <i class="fa fa-user"></i>
                                    </div>
                                    <input class="form-control" id="j_username" name="username" type="text" placeholder="<spring:message code="jpm.login.username" text="Username" />" autocorrect="off" autocapitalize="off" spellcheck="false" required/>
                                </div>

                                <div class="form-group">
                                    <label for="password" class="sr-only">Password</label>
                                    <div class="input-group mb-2">
                                        <div class="input-group-text">
                                            <i class="fa fa-key"></i>
                                        </div>
                                        <input class="form-control" type="password" name="password" id="password" placeholder="<spring:message code="jpm.login.password" text="Password" />"  required/>
                                    </div>
                                </div>
                                <div class="checkbox mb-3 mt-3">
                                    <label>
                                        <input type='checkbox' name='remember-me' /> <spring:message code="jpm.login.rememberme" text="Remember Me" />
                                    </label>
                                    <small>v${jpm.appversion}</small>
                                </div>
                                <c:if test="${not empty error}">
                                    <div class="alert alert-danger pagination-centered">${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}</div>
                                </c:if>
                                <button class="btn btn-lg bg-secondary w-100 text-white" type="submit"><spring:message code="jpm.login.submit" text="Login" /></button>
                                <div class="row">
                                    <div class="col-sm-12 mt-3">
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!--</main>-->
        <%@include  file="../jsp/inc/default-javascript.jsp" %>
        <script type="text/javascript">
            jpmLoad(function () {
                $("#j_username").trigger('focus');
                $("#loginform").on("submit", function () {
                    if ($("#j_username").val().trim() === "") {
                        $("#j_username").trigger('focus');
                        return false;
                    }
                });
            });
        </script>
    </body>
</html>