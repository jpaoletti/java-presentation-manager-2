<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html>
    <head>
        <%@include file="inc/default-head.jsp" %>
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
                                    <form class="form-horizontal" role="form" method="POST">
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
                                                    <input class="repeat" type="checkbox" value="true" name="repeat" ${not empty param.repeat?'checked':''} /> <spring:message code="jpm.operation.repeat.${operation.id}" text="?" />
                                                </div>
                                            </c:if>
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
                jpmLoad(function() {
                    jpmLoad(wrapToString);
                });
            </script>
    </body>
</html>