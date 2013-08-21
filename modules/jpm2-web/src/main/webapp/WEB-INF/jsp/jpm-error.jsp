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
                <div id="content-header">
                    <h1>${pm.subtitle}</h1>
                </div>
                <div id="breadcrumb">
                    <a href="#" title="" class="tip-bottom" class="current"><i class="glyphicon glyphicon-home"></i> <spring:message code="jpm.index.home" text="Home" /></a>
                    <a href="#" class="current"><spring:message code="jpm.menu.status" text="Status" /></a>
                </div>
                <div class="container-fluid">
                    <br/>
                    <div class="row">
                        <div class="alert alert-danger">
                            <ul>
                                <s:iterator value="actionErrors" var="e">
                                    <li>${e}</li>
                                    </s:iterator>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <%@include  file="inc/footer.jsp" %>
        <%@include  file="inc/default-javascript.jsp" %>
    </body>
</html>