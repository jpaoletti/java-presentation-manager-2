<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html>
    <head>
        <%@include file="inc/default-head.jsp" %>
        <link href="${cp}static/css/bootstrap-editable.css?v=${jpm.appversion}" rel="stylesheet" type="text/css" />
    </head>
    <c:set var="entityName" value="${entity.title}" />
    <spring:message var="operationName" code="${operation.title}" arguments="${entityName}" />
    <jpm:jpm-body>
        <%@include file="inc/default-content-header.jsp" %>
        <%@include file="inc/default-breadcrumb.jsp" %>
        <div class="container-fluid" id="container-${fn:replace(contextualEntity,'!', '-')}-${operation.id}">
            <div class="row"><br/>
                <div class="col-12">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <%@include file="inc/item-operations.jsp" %>
                        </div>
                        <div class="panel-body">
                            <form class="form-horizontal" role="form" action="#">
                                <%@include file="inc/default-form-content.jsp" %>
                                &nbsp;
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </jpm:jpm-body>
    <script type='text/javascript' src="${cp}static/js/bootstrap-editable.min.js?v=${jpm.appversion}" ></script>
    <script type="text/javascript">
        jpmLoad(function() {
            wrapToString();
            $(".inline-edit").editable({
                url: '${cp}jpm/${contextualEntity}/${instance.id}/iledit',
                send: "always",
                emptytext: "-"
            });
        });
    </script>
</html>