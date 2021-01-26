<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div id="${param.field}objectSearcher" class="objectSearcher">
    <label>
        <input type="checkbox" id="${param.field}selectAll" /> <spring:message code="jpm.converter.collection.selectall" text="Select all" />
    </label><br/><br/>
</div>
<script type="text/javascript">
    jpmLoad(function () {
        $(document).on("click", "#${param.field}selectAll", function () {
            $("input[name='value']").prop("checked", $(this).is(":checked"));
        });
        $.ajax({
            url: "${cp}jpm/${param.entityId}.json",
            dataType: "json",
            async: false,
            data: {
                textField: "${param.textField}",
                filter: "${param.filter}",
                ownerId: "${not empty owner?ownerId:''}"
            },
            success: function (data) {
                var ids = "${param.value}".split(",");
                $.each(data.results, function (i, item) {
                    var checked = ($.inArray(item.id, ids) >= 0) ? 'checked' : '';
                    $("#${param.field}objectSearcher").append("<label><input type='checkbox' " + checked + " value='" + item.id + "' name='value' />&nbsp;" + item.text + "</label><br/>");
                });
            }
        });
    });
</script>