<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html>
    <head>
        <%@include file="inc/default-head.jsp" %>
        <link href="${cp}static/css/signin.css?v=${jpm.appversion}" rel="stylesheet" type="text/css" />
    </head>
    <body class="text-center no-footer">
        <form class="form-signin" id="loginform" action="login" method="POST">
            <img src="<c:url value='/static/img/login.png'/>" alt="Login" />

            <h1 class="h3 mb-3 font-weight-normal"><spring:message code="jpm.login.title" text="Enter username and password to continue." /></h1>
            <label for="j_username" class="sr-only"><spring:message code="jpm.login.username" text="Username" /></label>
            <input class="form-control" id="j_username" name="username" type="text" placeholder="<spring:message code="jpm.login.username" text="Username" />" autocorrect="off" autocapitalize="off" spellcheck="false" required/>

            <label for="password" class="sr-only">Password</label>
            <input class="form-control" type="password" name="password" id="password" placeholder="<spring:message code="jpm.login.password" text="Password" />"  required/>
            <div class="checkbox mb-3">
                <label>
                    <input type='checkbox' name='remember-me' /> <spring:message code="jpm.login.rememberme" text="Remember Me" />
                </label>
            </div>
            <c:if test="${not empty error}">
                <div class="alert alert-danger pagination-centered">${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}</div>
            </c:if>
            <button class="btn btn-lg btn-primary btn-block" type="submit"><spring:message code="jpm.login.submit" text="Login" /></button>
        </form>
        <%@include  file="../jsp/inc/default-javascript.jsp" %>
        <script type="text/javascript">
            jpmLoad(function () {
                $("#j_username").trigger('focus');
            });
        </script>
    </body>
</html>
