<%@include file="inc/default-taglibs.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<table class="table table-bordered table-compact table-striped table-weak-list">
    <thead>
        <tr>
            <c:if test="${showOperations}">
                <th></th>
                </c:if>
            <th id='operation-column-header-info' class="visible-xs">
                <spring:message code="jpm.list.information" text="jpm.list.information" />
            </th>
            <c:forEach items="${paginatedList.fields}" var="field">
                <th data-field="${field.id}"  class="hidden-xs">
                    <jpm:field-title entity="${entity}" fieldId="${field.id}" />
                </th>
            </c:forEach>
        </tr>
    </thead>
    <tbody>
        <spring:message var="entityName" code="jpm.entity.title.${weakId}" text=" " />
        <c:forEach items="${paginatedList.contents}" var="item">
            <tr data-id="${item.id}">
                <c:if test="${showOperations}">
                    <th class="operation-list" style="width: ${(not compactOperations)?(fn:length(item.operations) * 30 + 25):40}px">
                        <div class="btn-group nowrap">
                            <c:if test="${not compactOperations}">
                                <c:forEach items="${item.operations}" var="o">
                                    <jpm:operation-link operation="${o}" clazz="btn btn-xs btn-default" contextualEntity="${contextualEntity}" instanceId="${item.id}" entityName="${entityName}" title="false" />
                                </c:forEach>
                            </c:if>

                            <c:if test="${compactOperations}">
                                <div class="btn-group">
                                    <button type="button" class="btn  btn-xs btn-default dropdown-toggle" data-toggle="dropdown">
                                        <span class="glyphicon glyphicon-cog"></span> <span class="caret"></span>
                                    </button>
                                    <ul class="dropdown-menu" role="menu">
                                        <c:forEach items="${item.operations}" var="o">
                                            <li>
                                                <jpm:operation-link operation="${o}" contextualEntity="${contextualEntity}" instanceId="${item.id}" entityName="${entityName}" title="true" />
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </div>
                            </c:if>
                        </div>
                    </th>
                </c:if>
                <td class="visible-xs">
                    <table class="table table-condensed borderless">
                        <tbody>
                            <c:forEach items="${item.values}" var="v">
                                <c:set var="convertedValue" value="${v.value}"/>
                                <c:set var="field" value="${v.key}" scope="request" />
                                <tr>
                                    <th><jpm:field-title entity="${entity}" fieldId="${field}" /></th>
                                    <td>
                                        <c:if test="${fn:startsWith(convertedValue, '@page:')}">
                                            <jsp:include page="converter/${fn:replace(convertedValue, '@page:', '')}" flush="true" />
                                        </c:if>
                                        <c:if test="${not fn:startsWith(convertedValue, '@page:')}">
                                            ${convertedValue}
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </td>
                <c:forEach items="${item.values}" var="v">
                    <td data-field="${v.key}" class="hidden-xs">
                        <c:set var="convertedValue" value="${v.value}"/>
                        <c:set var="field" value="${v.key}" scope="request" />
                        <c:if test="${fn:startsWith(convertedValue, '@page:')}">
                            <jsp:include page="converter/${fn:replace(convertedValue, '@page:', '')}" flush="true" />
                        </c:if>
                        <c:if test="${not fn:startsWith(convertedValue, '@page:')}">
                            ${convertedValue}
                        </c:if>
                    </td>
                </c:forEach>
            </tr>
        </c:forEach>
    </tbody>
</table>
<script type="text/javascript">
    $(".inline-edit").each(function () {
        $(this).editable({url: '${cp}jpm/${contextualEntity}/' + $(this).closest("tr").attr("data-id") + '/iledit', send: "always", emptytext: "-"});
    });
    //<c:forEach items="${paginatedList.fields}" var="f"><c:if test="${not empty f.align}">
    $("td[data-field='${f.id}']").css("text-align", "${f.align}");
    //</c:if><c:if test="${not empty f.width}">
    $("td[data-field='${f.id}'], th[data-field='${f.id}']").css("width", "${f.width}");
    //</c:if></c:forEach>
</script>