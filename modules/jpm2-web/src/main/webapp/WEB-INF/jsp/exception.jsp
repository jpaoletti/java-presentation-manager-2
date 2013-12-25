<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html>
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <body>
        <%@include file="inc/header.jsp" %>
        <div id="container">
            <div id="content">
                <div id="content-header"></div>
                <div class="container-fluid">
                    <div class="row">
                        <div class="col-lg-12 center" style="text-align: center;">
                            <h1><spring:message code="jpm.pmexception" text="Error" /></h1>
                            <div class="alert alert-danger">
                                <spring:message code="${message.key}" arguments="${message.arguments}" argumentSeparator=";" text="${message.key}" />
                            </div>
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