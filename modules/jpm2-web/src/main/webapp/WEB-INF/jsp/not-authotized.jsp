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
                <div class="col-12 center" style="text-align: center;"  id="access-denied-container">
                    <img alt="Access Denied" src="${cp}static/img/denied.png" id="access-denied" />
                    <h1><spring:message code="jpm.access.denied" text="Access Denied" /></h1>
                </div>
            </div>
        </div>
    </jpm:jpm-body>
</html>