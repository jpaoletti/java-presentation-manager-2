<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<label>
    <input type="checkbox" id="${param.field}selectAll" /> <spring:message code="jpm.converter.collection.selectall" text="Select all" />
</label><br/><br/>
<div id="${param.field}objectSearcher" class="objectSearcher collectionSearcherContainer">
</div>
<script type="text/javascript">
    jpmLoad(function () {
        $(document).on("click", "#${param.field}selectAll", function () {
            $("input[name='value']").prop("checked", $(this).is(":checked"));
        });
        $(document).on("click", "input[name='value']", function () {
            $("#${param.field}selectAll").prop("checked", $("input[name='value']:checked").length === $("input[name='value']").length);
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
                    $("#${param.field}objectSearcher").append("<label><input type='checkbox' " + checked + " value='" + item.id + "' name='value' />&nbsp;" + item.text + "</label>");
                });
                if (data.results.length > 0) {
                    $("#${param.field}objectSearcher").attr("style","grid-template-rows: repeat(" + Math.ceil(data.results.length / 2) + ", 1fr)");
                }
            }
        });
    });
</script>