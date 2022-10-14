<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="fieldValue" value="${show_object_field_value[field.concat(param.objectId)]}" />
<c:if test="${not empty fieldValue}">
    <c:if test="${not empty param.fields}">
        <a disabled href="javascript:;" id="field_${field}_${param.instanceId}_${param.objectId}" class="showObjectConverter">${fieldValue}<span class="fa fa-comment-alt pull-right"></span></a>
        <script type="text/javascript">
            $(document).on("click", "#field_${field}_${param.instanceId}_${param.objectId}", function () {
                var _this = $(this);
                $.getJSON("${cp}jpm/${param.entityId}/${param.instanceId}/show.json?fields=${param.fields}", function (data) {
                            var content = "<div><table class='table table-compact table-bordered'><tbody>";
                            $.each(data, function (i, v) {
                                content = content + "<tr><th>" + i + "</th><td>" + v + "</td></tr>";
                            });
                            content = content + "</tbody></table>";
                            content = content + "<button onclick=\"$(this).parents('div.popover').popover('hide');\" class='float-end btn btn-sm close'  type='button' ><i class='fas fa-times'></i></button>";
                            //<c:if test="${not empty param.operationLink}">
                            content = content + "<a href='${cp}${param.operationLink}'><i class='${param.operationIcon}'></i> ${param.operationTitle}</a>";
                            //</c:if>
                            content = content + "</div>";
                            _this.popover({placement: 'top', html: true, content: content}).popover('show');
                        });
                    });
            </script>
    </c:if>
    <c:if test="${empty param.fields}">
        <span class="to-string" title="${fieldValue}" data-align="null">${fieldValue}</span>
    </c:if>
</c:if>