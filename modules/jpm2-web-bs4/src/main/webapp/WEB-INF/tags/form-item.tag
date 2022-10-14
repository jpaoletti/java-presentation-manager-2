<%@tag description="Form Item" pageEncoding="UTF-8"%>
<%@include file="../jsp/inc/default-taglibs.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@attribute name = "id"    required="false" type="java.lang.String" %>
<%@attribute name = "label" required="false" type="java.lang.String" %>
<%@attribute name = "clazz" required="false" type="java.lang.String" %>
<%@attribute name = "entity" required="false" type="jpaoletti.jpm2.core.model.Entity" %>
<%@attribute name = "fieldId" required="false" type="java.lang.String" %>
<%@attribute name = "help" required="false" type="java.lang.String" %>
<%@attribute name = "displayTitle" required="false" type="java.lang.Boolean" %>
<div id="control-group-${id}" class="form-group row ${clazz}">
    <c:if test="${empty displayTitle or displayTitle}">
        <c:if test="${not empty label}">
            <label class="col-md-4 control-label" for="${id}">
                <c:if test="${not empty help}">
                    <a class='tip-top' data-toggle='tooltip' title='${help}' href='#' >${label}</a>
                </c:if>
                <c:if test="${empty help}">
                    ${label}
                </c:if>
            </label>
        </c:if>
        <c:if test="${empty label}">
            <label class="col-md-4 control-label" for="${id}">
                <jpm:field-title entity="${entity}" fieldId="${fieldId}" />
            </label>
        </c:if>
    </c:if>
    <div class="${(empty displayTitle or displayTitle)?'col-md-8':'col-md-12'} converted-field-container">
        <jsp:doBody />
    </div>
</div>