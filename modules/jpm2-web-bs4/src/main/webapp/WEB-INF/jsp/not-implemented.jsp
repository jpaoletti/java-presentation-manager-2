<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html>
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <jpm:jpm-body>
        <div id="content-header" class="page-header"></div>
        <div class="container-fluid">
            <div class="row">
                <div class="col-lg-12 center" style="text-align: center;">
                    <img alt="" src="${cp}static/img/notimplemented.png" id="not-implemented" />
                    <h1><spring:message code="jpm.not.implemented" text="Not implemented" /></h1>
                </div>
            </div>
            <div class="row">
                <div class="col-lg-11 col-lg-offset-1">
                    <br /><br /><br />
                    <a href="javascript:history.back();" class="btn btn-default"><span class="fas fa-arrow-left"></span> <spring:message code="jpm.back" text="Back" /></a>
                    <br /><br /><br />
                </div>
            </div>
        </div>
    </jpm:jpm-body>
</html>