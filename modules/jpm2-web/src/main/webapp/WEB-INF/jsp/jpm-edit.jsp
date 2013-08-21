<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html>
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <body>
        <spring:message var="entityName" code="${entity.title}" text="${entity.title}" />
        <spring:message var="operationName" code="${operation.title}" arguments="${entityName}" />
        <%@include file="inc/header.jsp" %>
        <%@include file="inc/menu.jsp" %>
        <div id="container">
            <div id="content">
                <%@include file="inc/default-content-header.jsp" %>
                <%@include file="inc/default-breadcrumb.jsp" %>

                <div class="container-fluid">
                    <div class="row"><br/>
                        <div class="col-12">
                            <div class="widget-box">
                                <div class="widget-title">
                                    <%@include file="inc/item-operations.jsp" %>
                                </div>
                                <div class="widget-content">
                                    <form class="form-horizontal" role="form" method="POST" action="${operation.id}!commit">
                                        <input name="entityId" value="${entity.id}" type="hidden" />
                                        <input name="instanceId" value="${instance.id}" type="hidden" />
                                        <c:if test="${empty entity.panels}">
                                            <s:iterator value="instance.values" var="value">
                                                <div id="control-group-${key}" class="form-group">
                                                    <label class="col-lg-2 control-label" for="f_${key}"><spring:message code="jpm.field.${entity.id}.${key}" text="${key}" /></label>
                                                    <div class="col-lg-10">
                                                        ${value}
                                                        <p class="help-block"><spring:message code="jpm.field.${entity.id}.${key}.help" text="" /></p>
                                                    </div>
                                                </div>
                                            </s:iterator>
                                        </c:if>
                                        <c:if test="${not empty ctx.entity.panels}">
                                            TO-DO
                                        </c:if>
                                        <div class="form-group">
                                            <div class="col-lg-offset-2 col-lg-10">
                                                <button type="submit" class="btn btn-primary"><spring:message code="jpm.form.submit" text="Submit" /></button>
                                            </div>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <%@include  file="inc/footer.jsp" %>
            <%@include  file="inc/default-javascript.jsp" %>
            <script type="text/javascript">
                $(function() {
                    $(".help-block:empty").remove();
                    $(".to-string").each(function() {
                        var v = $(this).html();
                        $(this).html("<input disabled class='form-control' type='text' value='" + v + "' />");
                    });
                });
            </script>
    </body>
</html>