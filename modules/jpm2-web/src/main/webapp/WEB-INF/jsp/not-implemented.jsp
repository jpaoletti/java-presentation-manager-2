<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html>
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <body>
        <%@include file="inc/header.jsp" %>
        <jsp:include page="inc/menu/${currentHome}-menu.jsp" />
        <div id="container">
            <div id="content">
                <div id="content-header"></div>
                <div class="container-fluid">
                    <div class="row">
                        <div class="col-12 center" style="text-align: center;">
                            <img alt="" src="${cp}static/img/notimplemented.png" id="not-implemented" />
                            <h1><spring:message code="jpm.not.implemented" text="Not implemented" /></h1>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-lg-11 col-lg-offset-1">
                            <br /><br /><br />
                            <a href="javascript:history.back();" class="btn btn-default"><span class="glyphicon glyphicon-arrow-left"></span> <spring:message code="jpm.back" text="Back" /></a>
                            <br /><br /><br />
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <%@include  file="inc/footer.jsp" %>
        <%@include  file="inc/default-javascript.jsp" %>
    </body>
</html>