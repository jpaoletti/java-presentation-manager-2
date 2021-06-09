<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<select name="field_${field}" id="field_${field}" class="objectConverterInput form-control">
    <c:forEach var="option" items="${fn:split(param.options, ',')}">
        <option value="${fn:split(option, '@')[0]}">${fn:split(option, '@')[1]}</option>
    </c:forEach>
</select>
<script type="text/javascript" src="${cp}static/js/select2.min.js?v=${jpm.appversion}"></script>
<script type="text/javascript">
    jpmLoad(function () {
        $("#field_${field}").val("${param.value}").select2({
            placeholder: "...",
            allowClear: true,
            width: 'resolve',
            dropdownCssClass: "bigdrop"
        });
    });
</script>