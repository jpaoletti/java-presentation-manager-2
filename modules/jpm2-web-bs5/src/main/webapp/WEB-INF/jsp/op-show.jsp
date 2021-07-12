<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html lang="${locale.language}">
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <jpm:jpm-body>
        <jpm:jpm-item-operation>
            <form class="form-horizontal" role="form" action="#">
                <%@include file="inc/default-form-content.jsp" %>
                &nbsp;
            </form>
        </jpm:jpm-item-operation>
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