<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html>
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <c:set var="entityName" value="${entity.title}" />
    <spring:message var="operationName" code="${operation.title}" arguments="${entityName}" />
    <jpm:jpm-body>
        <%@include file="inc/default-breadcrumb.jsp" %>
        <div class="container-fluid" id="container-${fn:replace(contextualEntity,'!', '-')}-${operation.id}">
            <div class="row"><br/>
                <div class="col-lg-12">
                    <div class="card">
                        <div class="card-header">
                            <div class="row">
                                <div class="col-md-6 col-sm-12">
                                    <%@include file="inc/item-operations.jsp" %>
                                </div>
                                <div class="col-md-6 col-sm-12">
                                    <%@include file="inc/general-operations.jsp" %>
                                </div>
                            </div>
                        </div>
                        <div class="card-body">
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
    <script type='text/javascript' src="${cp}static/js/jquery.jeditable.min.js?v=${jpm.appversion}" ></script>
    <script type="text/javascript">
        jpmLoad(function () {
            wrapToString();
            $(".inline-edit").each(function () {
                $(this).editable('${cp}jpm/${contextualEntity}/${instance.id}/iledit', {
                    placeholder: "-",
                    submitdata: {
                        name: $(this).attr("data-name")
                    }
                });
            });
            asynchronicOperationProgress("${contextualEntity}#${instance.id}");
                });
    </script>
</html>