<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<select name="field_${field}" id="field_${field}" class="objectConverterInput form-select" data-entity="${param.entityId}" data-textField="${param.textField}" data-related="${param.related}">
    <c:if test="${not empty param.value}">
        <option value="${param.value}">${param.valueText}</option>
    </c:if>
</select>
<c:if test="${not empty param.textFieldDetails}">
    <a disabled href="javascript:;" id="fieldDetails_${field}_${param.currentId}" class="objectConverterInputDetails"><spring:message code="jpm.converter.object.selected.details" text="Selected Details" /></a>
    <script type="text/javascript">
        $(document).on("click", "#fieldDetails_${field}_${param.currentId}", function () {
            var _this = $(this);
            let id = $("#field_${field}").val();
            if (id !== null && id !== "") {
                $.getJSON("${cp}jpm/${param.entityId}/" + id + "/show.json?fields=${param.textFieldDetails}", function (data) {
                    var content = "<div><div class='table-responsive'><table class='table table-bordered table-sm w-auto'><tbody>";
                    $.each(data, function (i, v) {
                        content = content + "<tr><th>" + i + "</th><td>" + v + "</td></tr>";
                    });
                    content = content + "</tbody></table>";
                    //<c:if test="${not empty param.textFieldDetailsOperation}">
                    content = content + "<a href='${cp}${param.operationLink}'><i class='${param.operationIcon}'></i> ${param.operationTitle}</a>";
                    //</c:if>
                    content = content + "</div>";
                    content = content + "<button onclick=\"$(this).parents('div.popover').popover('dispose');\" class='float-end btn btn-sm close'  type='button' ><i class='fas fa-times'></i></button>";
                    content = content + "</div>";
                    _this.popover({html: true, content: $(content)}).popover('show');
                });
            }
        });
        </script>
</c:if>
<script type="text/javascript" src="${cp}static/js/select2.min.js?v=${jpm.appversion}"></script>
<script type="text/javascript" src="${cp}static/js/locale/select2/${locale.language}.js?v=${jpm.appversion}"></script>
<script type="text/javascript">
        var jpmObjectConverter${field};
        jpmLoad(function () {
            var related = "${(not empty param.related)?param.related:''}".split(",");
            var relatedSelects = [];
            $.each(related, function (i, rel) {
                relatedSelects.push($("#field_" + rel));
            });
            jpmObjectConverter${field} = $("#field_${field}").buildJpmSelect2({
                entity: "${param.entityId}",
                field: "${field}",
                textField: "${param.textField}",
                placeholder: "${param.placeHolder}",
                minSearch:${param.minSearch},
                filter: "${param.filter}",
                currentId: "${param.currentId}",
                //owner: "${not empty owner?owner.id:''}",
                ownerId: "${not empty ownerId?ownerId:''}",
                related: ${(not empty param.related)?'$("#field_'.concat(param.related).concat('")'):'null'},
                sortBy: "${param.sortBy}",
                pageSize:${param.pageSize}
            });
            if (${param.readonly}) {
                $("#field_${field}").disableSelect2();
            }
        });</script>
    <c:if test="${param.addable}">
    <script type="text/javascript">
        jpmLoad(function () {
            $("#control-group-${field} label").append("<a href='${cp}jpm/${param.entityId}/add?close=true' target='_blank'>[<span class='fas fa-plus'></span>]</a>");
        });
    </script>
</c:if>