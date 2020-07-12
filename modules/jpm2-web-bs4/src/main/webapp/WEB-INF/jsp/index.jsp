<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html>
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <jpm:jpm-body>
        <div id="content-header" class="page-header">
            <h3>${jpm.subtitle}</h3>
        </div>
        <p>
            Welcome to Java Presentation Manager v2
        </p>
        <img  src="${cp}static/img/welcome.jpg" class="" style="max-width: 100%"/>
        <script type="text/javascript">
            jpmLoad(function() {
                $("#menu-home").addClass("active");
            });
        </script>
    </jpm:jpm-body>
</html>