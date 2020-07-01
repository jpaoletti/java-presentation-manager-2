<%@include file="inc/default-taglibs.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<table class="table table-bordered table-sm jpm-list-table w-auto">
    <thead class="thead-light">
        <tr>
            <c:if test="${showOperations}"><th></th></c:if>
                <c:forEach items="${paginatedList.fields}" var="field">
                <th data-field="${field.id}" >
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

                    <th class="operation-list" style="white-space:nowrap;width: ${(not compactOperations)?(fn:length(item.operations) * 30 + 25):((empty selectedOperations)?40:70)}px">
                        <div class="btn-group">
                            <c:if test="${not compactOperations}">
                                <c:forEach items="${item.operations}" var="o">
                                    <jpm:operation-link operation="${o}" clazz="btn btn-sm btn-secondary" contextualEntity="${contextualEntity}" instanceId="${item.id}" entityName="${entityName}" />
                                </c:forEach>
                            </c:if>
                            <c:if test="${compactOperations}">
                                <div class="btn-group">
                                    <button type="button" class="btn  btn-sm btn-light dropdown-toggle" data-toggle="dropdown">
                                        <span class="fas fa-cog"></span> <span class="caret"></span>
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
                <c:forEach items="${item.values}" var="v">
                    <td data-field="${v.key}" >
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
        $(this).editable('${cp}jpm/${contextualEntity}/' + $(this).closest("tr").attr("data-id") + '/iledit', {
            placeholder: "-",
            submitdata: {
                name: $(this).attr("data-name")
            }
        });
    });
    //<c:forEach items="${paginatedList.fields}" var="f"><c:if test="${not empty f.align}">
    $("td[data-field='${f.id}']").css("text-align", "${f.align}");
    //</c:if><c:if test="${not empty f.width}">
    $("td[data-field='${f.id}'], th[data-field='${f.id}']").css("width", "${f.width}");
    //</c:if></c:forEach>
</script>