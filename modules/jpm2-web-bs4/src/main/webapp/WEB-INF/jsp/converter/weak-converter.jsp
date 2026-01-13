<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<security:authorize access="hasAnyAuthority('${param.weakAuth}', 'SPECIAL')">
    <c:if test="${param.showBtn}">
        <a id="weak${field}" class="btn btn-info btn-xs text-nowrap" href="${cp}jpm/${contextualEntity}/${param.ownerId}/${param.weakId}${param.context}/list"><span class="${param.btnIcon}"></span>&nbsp;<spring:message code='${param.btnText}' text='Change' /></a><br/>
    </c:if>
    <c:if test="${param.showList}">
        <div id="weak${field}-list">
            <img alt="..." src="${cp}static/img/weakloading.gif"/>
        </div>
        <script type="text/javascript">
            jpmLoad(function () {
                $("#control-group-${field}").find(".col-lg-4").remove();
                $("#control-group-${field}").find(".col-lg-8").removeClass("col-lg-8").addClass("col-lg-12");
                $("#weak${field}-list").load("${cp}jpm/${contextualEntity}/${instance.id}/${param.weakId}${param.context}/weaklist?showOperations=${param.showOperations}");
            });
        </script>
    </c:if>
</security:authorize>
<security:authorize access="!hasAnyAuthority('${param.weakAuth}', 'SPECIAL')">
    <div class="alert alert-danger">
        <b><spring:message code="jpm.access.denied" />:</b> ${param.weakAuth}
    </div>
</security:authorize>