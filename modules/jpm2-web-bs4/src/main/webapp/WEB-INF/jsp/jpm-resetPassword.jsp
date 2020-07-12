<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html>
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <c:set var="entityName" value="${entity.title}" />
    <spring:message var="operationName" code="${operation.title}" arguments="${entityName}" />
    <jpm:jpm-body>
        <%@include file="inc/default-itemop-header.jsp" %>
        <div class="container-fluid">
            <div class="row"><br/>
                <div class="col-lg-12">
                    <form class="form-horizontal" role="form" action="#">
                        <div class="row jpm-content-panels">
                            <div class="col-sm-6">
                                <div id="control-group-${key}" class="form-group">
                                    <label class="col-lg-4 control-label" for="f_${key}">
                                        <spring:message code="jpm.security.generatedpassword" text="Generated Passowrd"/>
                                    </label>
                                    <div class="col-lg-8">
                                        <input type="text" class="form-control" value="${object.newPassword}" disabled/>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </jpm:jpm-body>
</html>