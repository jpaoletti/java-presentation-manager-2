<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="fieldValue" value="${show_object_field_value[field.concat(param.objectId)]}" />
<c:if test="${not empty fieldValue and param.instanceId != 'null'}">
    <c:if test="${not empty param.fields}">
        <a disabled href="javascript:;" id="field_${field}_${param.instanceId}_${param.objectId}" class="showObjectConverter">${fieldValue} <span class="fa fa-comment-alt float-end"></span></a>
        <script type="text/javascript">
            $(document).on("click", "#field_${field}_${param.instanceId}_${param.objectId}", function () {
                var _this = $(this);
                $.getJSON("${cp}jpm/${param.entityId}/${param.instanceId}/show.json?fields=${param.fields}", function (data) {
                            var content = "<div><div class='table-responsive'><table class='table table-bordered table-sm w-auto'><tbody>";
                            $.each(data, function (i, v) {
                                content = content + "<tr><th>" + i + "</th><td>" + v + "</td></tr>";
                            });
                            content = content + "</tbody></table></div>";
                            content = content + "<button onclick=\"$(this).parents('div.popover').popover('hide');\" class='float-end btn btn-sm close'  type='button' ><i class='fas fa-times'></i></button>";
                            //<c:if test="${not empty param.operationLink}">
                            content = content + "<a href='${cp}${param.operationLink}'><i class='${param.operationIcon}'></i> ${param.operationTitle}</a>";
                            //</c:if>
                            content = content + "</div>";
                            _this.popover({html: true, content: $(content)}).popover('show');
                        });
                    });
            </script>
    </c:if>
    <c:if test="${empty param.fields}">
        <span class="to-string" title="${fieldValue}" data-align="null">${fieldValue}</span>
    </c:if>
</c:if>
<c:if test="${empty fieldValue or param.instanceId == 'null'}">
    <span class="to-string">-</span>
</c:if>