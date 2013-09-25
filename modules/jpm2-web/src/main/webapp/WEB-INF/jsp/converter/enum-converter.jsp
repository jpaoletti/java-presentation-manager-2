<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<select name="field_${field}" id="field_${field}" class="objectConverterInput">
    <c:forEach var="option" items="${fn:split(param.options, ',')}">
        <option value="${fn:split(option, '@')[0]}">${fn:split(option, '@')[1]}</option>
    </c:forEach>
</select>
<script type="text/javascript" src="${cp}static/js/select2.min.js"></script>
<script type="text/javascript" src="${cp}static/js/locale/select2_locale_${locale.language}.js"></script>
<script type="text/javascript">
    jpmLoad(function() {
        if (!$("link[href='${cp}static/css/select2.css']").length) {
            $('<link href="${cp}static/css/select2.css" rel="stylesheet">').appendTo("head");
        }
        $("#field_${field}").val("${param.value}").select2({
            placeholder: "...", allowClear: true,
            width: 'copy', dropdownCssClass: "bigdrop",
        });
    });
</script>