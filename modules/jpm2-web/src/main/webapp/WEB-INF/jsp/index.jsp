<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html>
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <jpm:jpm-body>
        <div id="content-header" class="page-header" class="page-header">
            <h1>${jpm.subtitle}</h1>
        </div>
        <p>
            Welcome to Java Presentation Manager v2
        </p>
        <script type="text/javascript">
            jpmLoad(function() {
                $("#menu-home").addClass("active");
            });
        </script>
    </jpm:jpm-body>
</html>