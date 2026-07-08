<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html lang="${locale.language}">
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <jpm:jpm-body>
        <jpm:jpm-item-operation>
            <form class="form-horizontal" role="form" method="POST" id="jpmForm" enctype="multipart/form-data">
                <div class="row">
                    <div class="col-lg-12">
                        <label for="jsonFile"><strong>Archivo JSON</strong></label>
                        <input id="jsonFile" name="jsonFile" type="file" class="form-control" accept=".json,application/json" />
                    </div>
                </div>
                <div class="row mt-3">
                    <div class="col-lg-12">
                        <label for="json"><strong><c:out value="${operationTitle}" /> / JSON manual</strong></label>
                        <textarea id="json" name="json" class="form-control" rows="24"><c:out value="${json}" /></textarea>
                    </div>
                </div>
                <c:if test="${not empty entityMessages}">
                    <div class="row mt-3">
                        <div class="col-lg-12">
                            <div class="alert alert-danger">
                                <c:forEach items="${entityMessages}" var="m">
                                    <div><spring:message code="${m.key}" text="${m.key}" arguments="${m.arguments}" argumentSeparator=";" /></div>
                                </c:forEach>
                            </div>
                        </div>
                    </div>
                </c:if>
                <div class="row mt-3">
                    <div class="col-lg-12">
                        <button type="submit" class="btn btn-success">
                            <spring:message code="jpm.form.submit" text="Submit" />
                        </button>
                    </div>
                </div>
            </form>
        </jpm:jpm-item-operation>
    </jpm:jpm-body>
    <script type="text/javascript">
        jpmLoad(function () {
            buildAjaxJpmForm();
        });
    </script>
</html>
