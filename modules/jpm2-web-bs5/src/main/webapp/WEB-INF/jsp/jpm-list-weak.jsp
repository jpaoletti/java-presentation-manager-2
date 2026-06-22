<%@include file="inc/default-taglibs.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:set var="weakListDomId" value="${fn:replace(ownerEntityId, '!', '_')}_${ownerInstanceId}_${fn:replace(weakEntityId, '!', '_')}" />
<c:set var="weakListBaseUrl" value="${not empty weakListBaseUrl ? weakListBaseUrl : cp.concat('jpm/').concat(ownerEntityId).concat('/').concat(ownerInstanceId).concat('/').concat(weakEntityId).concat('/weaklist?showOperations=').concat(showOperations).concat('&paginated=').concat(weakListPaginated)}" />
<c:set var="weakListLoadUrl" value="${not empty weakListLoadUrl ? weakListLoadUrl : weakListBaseUrl}" />
<c:set var="setVisibleColumnsUrl" value="${cp}jpm/${ownerEntityId}/${ownerInstanceId}/${weakEntityId}/setWeakVisibleColumns" />
<div id="weak-list-wrapper-${weakListDomId}">
<table class="table table-bordered table-sm jpm-list-table w-auto">
    <thead class="table-secondary">
        <tr>
            <th>
                <button class="btn btn-primary btn-sm float-end weak-columns-modal-show" type="button" data-bs-target="#weak-columns-modal-${weakListDomId}">
                    <span class="fas fa-eye"></span>
                </button>
            </th>
            <c:forEach items="${paginatedList.fields}" var="field">
                <c:if test="${visibleColumns.contains(field.id)}">
                <th data-field="${field.id}" >
                    <jpm:field-title entity="${entity}" fieldId="${field.id}" />
                </th>
                </c:if>
            </c:forEach>
        </tr>
    </thead>
    <tbody>
        <spring:message var="entityName" code="jpm.entity.title.${weakId}" text=" " />
        <c:forEach items="${paginatedList.contents}" var="item">
            <tr data-id="${item.id}" class="instance-row ${not fn:contains(item.highlight, ':') ? item.highlight : ''}" ${fn:contains(item.highlight, ':') ? 'style="'.concat(item.highlight).concat('"') : ''}>
                <c:if test="${showOperations}">
                    <th class="operation-list" style="white-space:nowrap;width: ${(not compactOperations)?(fn:length(item.operations) * 30 + 25):((empty selectedOperations)?40:70)}px">
                        <div class="btn-group">
                            <c:if test="${not compactOperations}">
                                <c:forEach items="${item.operations}" var="o">
                                    <jpm:operation-link operation="${o}" clazz="btn btn-sm ${not empty o.color?o.color:'btn-secondary'}" contextualEntity="${contextualEntity}" instanceId="${item.id}" entityName="${entityName}" />
                                </c:forEach>
                            </c:if>
                            <c:if test="${compactOperations}">
                                <div class="dropdown">
                                    <button type="button" class="btn  btn-sm btn-light dropdown-toggle" data-bs-toggle="dropdown">
                                        <span class="fas fa-cog"></span>
                                    </button>
                                    <ul class="dropdown-menu" role="menu">
                                        <c:forEach items="${item.operations}" var="o">
                                            <li><jpm:operation-link operation="${o}" contextualEntity="${contextualEntity}" instanceId="${item.id}" entityName="${entityName}" title="true" clazz="dropdown-item" /></li>
                                            </c:forEach>
                                    </ul>
                                </div>
                            </c:if>
                        </div>
                    </th>
                </c:if>
                <c:if test="${not showOperations}">
                    <th class="operation-list" style="white-space:nowrap;width: 40px"></th>
                </c:if>
                <c:forEach items="${item.values}" var="v">
                    <c:if test="${visibleColumns.contains(v.key)}">
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
                    </c:if>
                </c:forEach>
            </tr>
        </c:forEach>
    </tbody>
    <c:if test="${weakListPaginated}">
        <tfoot>
            <tr>
                <td colspan="${paginatedList.fields.size() + 1}">
                    <div class="weak-pagination list-pagination">
                        <%@include file="inc/default-paginator.jsp" %>
                    </div>
                    <c:if test="${entity.countable}">
                        <div class="float-end mt-1 me-1">
                            <span class="badge bg-secondary"><spring:message code="jpm.list.total" text="Total: ${paginatedList.total}" arguments="${paginatedList.total}" /></span>
                        </div>
                    </c:if>
                </td>
            </tr>
        </tfoot>
    </c:if>
</table>
</div>
<div class="modal fade" id="weak-columns-modal-${weakListDomId}" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <form method="POST" class="weak-visible-columns-form" action="${setVisibleColumnsUrl}" data-reload-url="${weakListLoadUrl}">
                <div class="modal-header">
                    <h5 class="modal-title"><spring:message code="jpm.list.columnmodal.title" text="Column Visualization" /></h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <c:forEach var="field" items="${paginatedList.fields}">
                        <label><input type="checkbox" value="${field.id}" name="column" class="columnVisibleChk" ${visibleColumns.contains(field.id)?'checked':''}> <jpm:field-title entity="${entity}" fieldId="${field.id}" /></label><br/>
                    </c:forEach>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-danger" data-bs-dismiss="modal"><spring:message code="jpm.list.modalsearch.close" text="Close" /></button>
                    <button type="submit" class="btn btn-success"><spring:message code="jpm.list.modalsearch.ok" text="Ok" /></button>
                </div>
            </form>
        </div>
    </div>
</div>
<script type="text/javascript">
    (function () {
        var weakColumnsModalId = "weak-columns-modal-${weakListDomId}";
        var weakColumnsModalElement = document.getElementById(weakColumnsModalId);
        var weakListBaseUrl = "${weakListBaseUrl}";

        function reloadWeakList(extraQuery) {
            var url = weakListBaseUrl;
            if (extraQuery) {
                url += (url.indexOf("?") === -1 ? "?" : "&") + extraQuery.replace(/^\?/, "");
            }
            $("#weak-list-wrapper-${weakListDomId}").parent().load(url);
        }

        $(document.body).children("#" + weakColumnsModalId).not(weakColumnsModalElement).each(function () {
            var staleModal = bootstrap.Modal.getInstance(this);
            if (staleModal) {
                staleModal.dispose();
            }
            $(this).remove();
        });
        if (weakColumnsModalElement && weakColumnsModalElement.parentElement !== document.body) {
            document.body.appendChild(weakColumnsModalElement);
        }

        $("#weak-list-wrapper-${weakListDomId} .inline-edit").each(function () {
            $(this).editable('${cp}jpm/${contextualEntity}/' + $(this).closest("tr").attr("data-id") + '/iledit', {
                placeholder: "-",
                submitdata: {
                    name: $(this).attr("data-name")
                }
            });
        });
        $("#weak-list-wrapper-${weakListDomId} a").each(function () {
            if ($(this).attr("href")) {
                $(this).attr("href", $(this).attr("href").replace("@cp@/", getContextPath()));
            }
        });
        $("#weak-list-wrapper-${weakListDomId} .weak-pagination a").off("click").on("click", function (e) {
            var href = $(this).attr("href");
            if (!href || href === "#" || $(this).closest(".disabled").length) {
                e.preventDefault();
                return;
            }
            e.preventDefault();
            reloadWeakList(href);
        });
        $("#weak-list-wrapper-${weakListDomId} .weak-pagination form").off("submit").on("submit", function (e) {
            e.preventDefault();
            reloadWeakList($(this).serialize());
        });
        $(".weak-columns-modal-show[data-bs-target='#weak-columns-modal-${weakListDomId}']").off("click").on("click", function () {
            bootstrap.Modal.getOrCreateInstance(weakColumnsModalElement).show();
        });
        $("#weak-columns-modal-${weakListDomId} .weak-visible-columns-form").off("submit").on("submit", function (e) {
            e.preventDefault();
            var form = $(this);
            var container = $("#weak-list-wrapper-${weakListDomId}").parent();
            $.post(form.attr("action"), form.serialize(), function () {
                var weakColumnsModal = bootstrap.Modal.getOrCreateInstance(weakColumnsModalElement);
                $(weakColumnsModalElement).off("hidden.bs.modal.jpmWeakReload").on("hidden.bs.modal.jpmWeakReload", function () {
                    weakColumnsModal.dispose();
                    $(weakColumnsModalElement).remove();
                    $(".modal-backdrop").remove();
                    $("body").removeClass("modal-open");
                    $("body").css("padding-right", "");
                    container.load(form.data("reload-url"));
                });
                weakColumnsModal.hide();
            });
        });
        //<c:forEach items="${paginatedList.fields}" var="f"><c:if test="${not empty f.align}">
        $("#weak-list-wrapper-${weakListDomId} td[data-field='${f.id}']").css("text-align", "${f.align}");
        //</c:if><c:if test="${not empty f.width}">
        $("#weak-list-wrapper-${weakListDomId} td[data-field='${f.id}'], #weak-list-wrapper-${weakListDomId} th[data-field='${f.id}']").css("width", "${f.width}");
        //</c:if></c:forEach>
    })();
</script>
