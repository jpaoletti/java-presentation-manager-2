<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html lang="${locale.language}">
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <jpm:jpm-body>
        <div id="content-header" class="page-header">
            <h1>${jpm.subtitle}</h1>
        </div>
        <div id="breadcrumb">
            <a href="javascript: void(0)" title="" class="tip-bottom" class="current"><i class="glyphicon glyphicon-home"></i> <spring:message code="jpm.index.home" text="Home" /></a>
            <a href="javascript: void(0)" class="current"><spring:message code="jpm.menu.status" text="Status" /></a>
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
    </jpm:jpm-body>
</html>