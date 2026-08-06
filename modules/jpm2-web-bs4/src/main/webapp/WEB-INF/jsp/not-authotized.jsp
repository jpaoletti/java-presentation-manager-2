<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html lang="${locale.language}">
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <jpm:jpm-body>
        <div id="content-header" class="page-header"></div>
        <div class="container-fluid">
            <div class="row">
                <c:choose>
                    <%-- An operation condition was not met and explains itself --%>
                    <c:when test="${not empty message}">
                        <div class="col-lg-8 offset-lg-2">
                            <div class="alert alert-warning d-flex align-items-center justify-content-between" role="alert" id="condition-not-met">
                                <div class="d-flex align-items-center">
                                    <span class="fas fa-exclamation-triangle fa-lg mr-3"></span>
                                    <span><spring:message code="${message.key}" arguments="${message.arguments}" argumentSeparator=";" text="${message.key}" /></span>
                                </div>
                                <c:if test="${not empty goToOperation}">
                                    <jpm:operation-link operation="${goToOperation}"
                                                        clazz="btn btn-sm ${not empty goToOperation.color?goToOperation.color:'btn-outline-dark'} ml-3 text-nowrap"
                                                        contextualEntity="${contextualEntity}"
                                                        instanceId="${goToInstanceId}"
                                                        entityName="${entity.title}"
                                                        title="true" />
                                </c:if>
                            </div>
                        </div>
                    </c:when>
                    <%-- Plain authorization failure --%>
                    <c:otherwise>
                        <div class="col-lg-12 center" style="text-align: center;"  id="access-denied-container">
                            <img alt="Access Denied" src="${cp}static/img/denied.png" id="access-denied" />
                            <h1><spring:message code="jpm.access.denied" text="Access Denied" /></h1>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </jpm:jpm-body>
</html>