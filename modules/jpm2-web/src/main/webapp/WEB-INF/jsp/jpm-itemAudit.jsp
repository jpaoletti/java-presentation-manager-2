<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
                <div class="container-fluid">
                    <div class="row"><br/>
                        <div class="col-12">
                            <div class="widget-box">
                                <div class="widget-title">
                                    <%@include file="inc/item-operations.jsp" %>
                                </div>
                                <div class="widget-content">
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
        </div>
        <%@include  file="inc/footer.jsp" %>
        <%@include  file="inc/default-javascript.jsp" %>
    </body>
</html>