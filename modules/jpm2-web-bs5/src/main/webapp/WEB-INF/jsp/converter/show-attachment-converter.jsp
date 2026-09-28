<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:if test="${not empty param.len}">
    <%--<spring:message code="jpm.converter.showfile.bytes.text" text="File: ?" arguments="${param.len}" /> --%>
    <span data-downloadable="${fn:escapeXml(param.downloadable)}" data-type="${fn:escapeXml(param.contentType)}" data-id="${fn:escapeXml(param.noteId)}" data-entity="${fn:escapeXml(param.entity)}" class="viewAttachmentIco"><i class="fas fa-search"></i>&nbsp;${fn:escapeXml(param.attachmentName)} (${fn:escapeXml(param.len)})</span>
</c:if>
<c:if test="${empty param.len}">
    <input disabled="" class="form-control" type="text" value='<spring:message code="jpm.converter.file.null.file.text" text="-" />' />
</c:if>