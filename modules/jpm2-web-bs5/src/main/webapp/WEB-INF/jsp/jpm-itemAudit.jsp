<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html lang="${locale.language}">
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <jpm:jpm-body>
        <jpm:jpm-item-operation>
            <div>
                <spring:message code="jpm.audit.dateFormat" var="dateFormat" text="yyyy/MM/dd HH:mm:ss" />
                <table class="table table-bordered table-sm">
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
        </jpm:jpm-item-operation>
    </jpm:jpm-body>
</html>