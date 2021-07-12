<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div id="${param.field}CollectionSearcherContainer">

</div>
<script type="text/javascript">
    jpmLoad(function () {
        $.ajax({
            url: "${cp}jpm/${param.entityId}.json",
            dataType: "json",
            async: false,
            data: {
                textField: "${param.textField}",
                filter: "${param.filter}"
            },
            success: function (data) {
                $.each(data.results, function (i, item) {
                    $("#${param.field}CollectionSearcherContainer").append("<label><input type='checkbox' value='" + item.id + "' name='value' />&nbsp;" + item.text + "</label><br/>");
                });
            }
        });
    });
</script>