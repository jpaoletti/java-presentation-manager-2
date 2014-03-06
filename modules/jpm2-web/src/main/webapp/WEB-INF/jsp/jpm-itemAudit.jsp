<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <c:set var="entityName" value="${entity.title}" />
    <spring:message var="operationName" code="${operation.title}" arguments="${entityName}" />
    <jpm:jpm-body>
        <%@include file="inc/default-content-header.jsp" %>
        <%@include file="inc/default-breadcrumb.jsp" %>
        <div class="container-fluid">
            <div class="row"><br/>
                <div class="col-12">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <%@include file="inc/item-operations.jsp" %>
                        </div>
                        <div class="panel-body">
                            <div>
                                <spring:message code="jpm.audit.dateFormat" var="dateFormat" text="yyyy/MM/dd HH:mm:ss" />
                                <table class="table table-bordered table-compact">
                                    <thead>
                                        <tr>
                                            <th style="width: 180px"><spring:message code="jpm.audit.date" /></th>
                                            <th style="width: 100px"><spring:message code="jpm.audit.operation" /></th>
                                            <th style="width: 100px"><spring:message code="jpm.audit.username" /></th>
                                            <th><spring:message code="jpm.audit.observations" /></th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${audits}" var="a">
                                            <spring:message code="jpm.operation.${a.operation}" var="opTitle" />
                                            <tr>
                                                <td><fmt:formatDate value="${a.datetime}" pattern="${dateFormat}" /></td>
                                                <td>${fn:replace(opTitle,'{0}','')}</td>
                                                <td>${a.username}</td>
                                                <td>${a.observations}</td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </jpm:jpm-body>
</html>