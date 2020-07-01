<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${not empty param.len}">
    <div class="input-group">
        <input disabled="" class="form-control" type="text" value='<spring:message code="jpm.converter.showfile.bytes.text" text="File: ?" arguments="${param.len}" />' />
        <c:if test="${param.downloadable}">
            <div class="input-group-addon">
                <a href="${cp}jpm/${entity.id}/${param.instanceId}/download/${field}?contentType=${param.contentType}&prefix=${param.prefix}&sufix=${param.sufix}"><span class="fas fa-download"></span></a>
            </div>
        </c:if>
    </div>
</c:if>
<c:if test="${empty param.len}">
    <spring:message code="jpm.converter.file.null.file.text" text="-" />
</c:if>