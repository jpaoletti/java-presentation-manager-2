<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<label>
    <input type="checkbox" id="${field}selectAll" /> <spring:message code="jpm.converter.collection.selectall" text="Select all" />
</label><br/><br/>
<c:forEach var="option" items="${fn:split(param.options, ',')}">
    <input type='checkbox' ${fn:split(option, '@')[2] == '1' ? 'checked':''} value='${fn:split(option, '@')[0]}' name='field_${field}' />&nbsp;${fn:split(option, '@')[1]} <br/>
</c:forEach>
<script type="text/javascript">
    jpmLoad(function () {
        $(document).on("click", "#${field}selectAll", function () {
            $("input[name='field_${field}']").prop("checked", $(this).is(":checked")).trigger("change");
        });
        $(document).on("click", "input[name='field_${field}']", function () {
            $("#${field}selectAll").prop("checked", $("input[name='field_${field}']:checked").length === $("input[name='field_${field}']").length);
        });
    });
</script>