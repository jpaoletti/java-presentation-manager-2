<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html>
    <head>
        <%@include file="inc/default-head.jsp" %>
        <link href="${cp}static/css/bootstrap-editable.css?v=${jpm.appversion}" rel="stylesheet" type="text/css" />
    </head>
    <body>
        <c:set var="entityName" value="${entity.title}" />
        <spring:message var="operationName" code="${operation.title}" arguments="${entityName}" />
        <%@include file="inc/header.jsp" %>
        <jsp:include page="inc/menu/${currentHome}-menu.jsp" />
        <div id="container">
            <div id="content">
                <%@include file="inc/default-content-header.jsp" %>
                <%@include file="inc/default-breadcrumb.jsp" %>
                <div class="container-fluid" id="container-${fn:replace(contextualEntity,'!', '-')}-${operation.id}">
                    <div class="row"><br/>
                        <div class="col-12">
                            <div class="widget-box">
                                <div class="widget-title">
                                    <%@include file="inc/item-operations.jsp" %>
                                </div>
                                <div class="widget-content">
                                    <form class="form-horizontal" role="form" action="#">
                                        <%@include file="inc/default-form-content.jsp" %>
                                        &nbsp;
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <%@include  file="inc/footer.jsp" %>
        <%@include  file="inc/default-javascript.jsp" %>
        <script type='text/javascript' src="${cp}static/js/bootstrap-editable.min.js?v=${jpm.appversion}" ></script>
        <script type="text/javascript">
            jpmLoad(wrapToString);
            jpmLoad(function() {
                $(".inline-edit").each(function() {
                    $(this).editable({
                        url: '${cp}jpm/${contextualEntity}/${instance.id}/iledit',
                        send: "always",
                        emptytext: "-"
                    });
                });
            });
        </script>
    </body>
</html>