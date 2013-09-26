<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:if test="${param.showBtn}">
    <a id="weak${field}" class="btn btn-default" href="${cp}jpm/${entity.id}/${param.ownerId}/${param.weakId}/list"><i class="glyphicon ${param.btnIcon}"></i>&nbsp;<spring:message code='${param.btnText}' text='Change' /></a><br/>
</c:if>
<c:if test="${param.showList}">
    <div id="weak${field}-list">
        <img alt="..." src="${cp}static/img/weakloading.gif"/>
    </div>
    <script type="text/javascript">
        jpmLoad(function() {
            $("#control-group-${field}").find(".col-lg-4").remove();
            $("#control-group-${field}").find(".col-lg-8").removeClass("col-lg-8").addClass("col-lg-12");
            $("#weak${field}-list").load("${cp}jpm/${entity.id}/${instance.id}/${param.weakId}/weaklist");
        });
    </script>
</c:if>