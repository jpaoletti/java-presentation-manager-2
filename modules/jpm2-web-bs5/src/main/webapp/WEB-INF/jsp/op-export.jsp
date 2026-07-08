<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html lang="${locale.language}">
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <jpm:jpm-body>
        <jpm:jpm-item-operation>
            <div class="row">
                <div class="col-lg-12">
                    <label for="json"><strong><c:out value="${operationTitle}" /></strong></label>
                    <textarea id="json" class="form-control" rows="24" readonly="readonly"><c:out value="${json}" /></textarea>
                </div>
            </div>
            <div class="row mt-3">
                <div class="col-lg-12">
                    <button type="button" class="btn btn-success" id="downloadJsonBtn">
                        <spring:message code="jpm.form.submit" text="Download" />
                    </button>
                </div>
            </div>
        </jpm:jpm-item-operation>
    </jpm:jpm-body>
    <script type="text/javascript">
        jpmLoad(function () {
            $("#downloadJsonBtn").on("click", function () {
                const json = $("#json").val();
                const blob = new Blob([json], {type: "application/json;charset=utf-8"});
                const url = URL.createObjectURL(blob);
                const a = document.createElement("a");
                a.href = url;
                a.download = "${fileName}";
                document.body.appendChild(a);
                a.click();
                a.remove();
                URL.revokeObjectURL(url);
            });
        });
    </script>
</html>
