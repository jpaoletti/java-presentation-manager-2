<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html lang="${locale.language}">
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <jpm:jpm-body>
        <jpm:jpm-item-operation>
            <c:choose>
                <%-- An operation condition was not met and explains itself --%>
                <c:when test="${not empty message}">
                    <div class="row justify-content-center">
                        <div class="col-lg-8">
                            <div class="alert alert-warning d-flex align-items-center justify-content-between" role="alert" id="condition-not-met">
                                <div class="d-flex align-items-center">
                                    <span class="fas fa-triangle-exclamation fa-lg me-3"></span>
                                    <span><spring:message code="${message.key}" arguments="${message.arguments}" argumentSeparator=";" text="${message.key}" /></span>
                                </div>
                                <c:if test="${not empty goToOperation}">
                                    <jpm:operation-link operation="${goToOperation}"
                                                        clazz="btn btn-sm ${not empty goToOperation.color?goToOperation.color:'btn-outline-dark'} ms-3 text-nowrap"
                                                        contextualEntity="${contextualEntity}"
                                                        instanceId="${goToInstanceId}"
                                                        entityName="${entity.title}"
                                                        title="true" />
                                </c:if>
                            </div>
                        </div>
                    </div>
                </c:when>
                <%-- Plain authorization failure --%>
                <c:otherwise>
                    <div class="row">
                        <div class="col-lg-12 center" style="text-align: center;"  id="access-denied-container">
                            <img alt="Access Denied" src="${cp}static/img/denied.png" id="access-denied" class="mb-3" />
                            <h1><spring:message code="jpm.access.denied" text="Access Denied" /></h1>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </jpm:jpm-item-operation>
    </jpm:jpm-body>
</html>
