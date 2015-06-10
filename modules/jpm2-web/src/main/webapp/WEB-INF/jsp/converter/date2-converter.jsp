<div id="field_${field}" class="input-group">
    <input data-format="${param.format}" type="text" name="field_${field}" class="form-control" value="${param.value}" />
    <span class="input-group-addon add-on"><i class="glyphicon glyphicon-calendar"></i></span>
</div>
<script type="text/javascript" src="${cp}static/js/bootstrap-datetimepicker.min.js?v=${jpm.appversion}"></script>
<script type="text/javascript" src="${cp}static/js/locale/bootstrap-datepicker.${pageContext.response.locale}.js?v=${jpm.appversion}"></script>
<script type="text/javascript">
    jpmLoad(function() {
        if (!$("link[href='${cp}static/css/bootstrap-datetimepicker.min.css']").length) {
            $('<link href="${cp}static/css/bootstrap-datetimepicker.min.css" rel="stylesheet">').appendTo("head");
        }
        $('#field_${field}').datetimepicker({pickTime: false, language: "${pageContext.response.locale}"});
    });
</script>