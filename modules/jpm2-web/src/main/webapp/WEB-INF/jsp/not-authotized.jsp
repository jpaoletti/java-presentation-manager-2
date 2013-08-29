<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html>
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <body>
        <%@include file="inc/header.jsp" %>
        <%@include file="inc/menu.jsp" %>
        <div id="container">
            <div id="content">
                <div id="content-header"></div>
                <div class="container-fluid">
                    <div class="row">
                        <div class="col-12 center" style="text-align: center;">
                            <img alt="Access Denied" src="${cp}static/img/denied.png" id="access-denied" />
                            <h1><spring:message code="jpm.access.denied" text="Access Denied" /></h1>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <%@include  file="inc/footer.jsp" %>
        <%@include  file="inc/default-javascript.jsp" %>
    </body>
</html>