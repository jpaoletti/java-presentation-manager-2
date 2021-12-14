<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<select name="field_${field}" id="field_${field}" class="objectConverterInput form-select" data-entity="${param.entityId}" data-textField="${param.textField}" data-related="${param.related}">
    <c:if test="${not empty param.value}">
        <option value="${param.value}">${param.valueText}</option>
    </c:if>
</select>
<script type="text/javascript" src="${cp}static/js/select2.min.js?v=${jpm.appversion}"></script>
<script type="text/javascript" src="${cp}static/js/locale/select2/${locale.language}.js?v=${jpm.appversion}"></script>
<script type="text/javascript">
    jpmLoad(function () {
        $("#field_${field}").buildJpmSelect2({
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
    });
</script>
<c:if test="${param.addable}">
    <script type="text/javascript">
        jpmLoad(function () {
            $("#control-group-${field} label").append("<a href='${cp}jpm/${param.entityId}/add?close=true' target='_blank'>[<span class='fas fa-plus'></span>]</a>");
        });
    </script>
</c:if>