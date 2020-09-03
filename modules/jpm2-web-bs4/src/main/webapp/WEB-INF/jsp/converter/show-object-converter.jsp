<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${not empty param.value}">
    <c:if test="${not empty param.fields}">
        <a disabled href="javascript:;" id="field_${field}_${param.instanceId}_${param.objectId}" >${param.value} <span class="fa fa-comment pull-right"></span></a>
        <script type="text/javascript">
            $("body").on("click", "#field_${field}_${param.instanceId}_${param.objectId}", function () {
                var _this = $(this);
                $.getJSON("${cp}jpm/${param.entityId}/${param.instanceId}/show.json?fields=${param.fields}", function (data) {
                            var content = "<div class='table-responsive'><table class='table table-bordered table-sm w-auto'><tbody>";
                            $.each(data, function (i, v) {
                                content = content + "<tr><th>" + i + "</th><td>" + v + "</td></tr>";
                            });
                            content = content + "</tbody></table></div><button onclick=\"$(this).parents('div.popover').popover('hide');\" class='pull-right close'  type='button' ><i class='fas fa-times'></i></button>";
                            //<c:if test="${not empty param.operationLink}">
                            content = content + "<a href='${cp}${param.operationLink}'><i class='${param.operationIcon}'></i> ${param.operationTitle}</a>";
                            //</c:if>
                            console.log(content);
                            _this.popover({html: true, content: $(content)}).popover('show');
                        });
                    });
            </script>
    </c:if>
    <c:if test="${empty param.fields}">
        ${param.value}
    </c:if>
</c:if>