<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html lang="${locale.language}">
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <c:set var="entityName" value="${entity.title}" />
    <spring:message var="operationName" code="${operation.title}" arguments="${entityName}" />
    <jpm:jpm-body>
        <%@include file="inc/default-content-header.jsp" %>
        <%@include file="inc/default-breadcrumb.jsp" %>
        <div class="container-fluid" id="container-${fn:replace(contextualEntity,'!', '-')}-${operation.id}">
            <div class="row"><br/>
                <div class="col-lg-12">
                    <div class="panel panel-default" id="jpmMainPanel">
                        <div class="panel-heading">
                            <%@include file="inc/item-operations.jsp" %>
                        </div>
                        <div class="panel-body">
                            <form class="form-horizontal" role="form" method="POST" id="jpmForm">
                                <input name="entityId" value="${entity.id}" type="hidden" />
                                <input name="instanceId" value="${instance.id}" type="hidden" />
                                <%@include file="inc/default-form-content.jsp" %>
                                <c:if test="${not empty entityMessages}">
                                    <div class="row">
                                        <div class="col-lg-offset-2 col-lg-10">
                                            <div class="alert alert-danger">
                                                <c:forEach items="${entityMessages}" var="m">
                                                    * <spring:message code="${m.key}" text="${m.key}" arguments="${m.arguments}" argumentSeparator=";" />${!st.last ? '<br/>':''}
                                                </c:forEach>
                                            </div>
                                        </div>
                                    </div>
                                </c:if>
                                <div class="row">
                                    <div class="col-lg-1">
                                        <button type="submit" class="btn btn-primary"><spring:message code="jpm.form.submit" text="Submit" /></button>
                                    </div>
                                    <c:if test="${operation.repeatable}">
                                        <div class="col-lg-11">
                                            <input class="repeat" type="checkbox" value="true" name="repeat" ${not empty param.repeated?'checked':''} /> <spring:message code="jpm.operation.repeat.${operation.id}" text="?" />
                                        </div>
                                    </c:if>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </jpm:jpm-body>
    <script type="text/javascript" src="${cp}static/js/jquery.form.min.js?v=${jpm.appversion}"></script>
    <script type="text/javascript">
        jpmLoad(function () {
            wrapToString();
        });
    </script>
    <c:if test="${not close}">
        <script type="text/javascript">
            jpmLoad(function () {
                buildAjaxJpmForm();
            });
        </script>
    </c:if>
    <c:if test="${close}">
        <script type="text/javascript">
            jpmLoad(function () {
                buildAjaxJpmForm("jpmForm", function (data) {
                    data.next = null;
                    processFormResponse(data);
                    if (data.ok) {
                        setTimeout(function () {
                            window.close();
                        }, 2000);
                    }
                });
            });
        </script>
    </c:if>
</html>