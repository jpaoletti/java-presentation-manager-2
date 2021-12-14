<div id="field_${field}" class="input-group">
    <input type="text" name="field_${field}" class="form-control" value="${param.value}" />
    <span class="input-group-addon add-on"><i class="glyphicon glyphicon-calendar"></i></span>
</div>
<script type="text/javascript" src="${cp}static/js/moment.min.js?v=${jpm.appversion}" charset="UTF-8"></script>
<script type="text/javascript" src="${cp}static/js/bootstrap-datetimepicker.min.js?v=${jpm.appversion}"></script>
<script type="text/javascript">
    jpmLoad(function () {
        $('#field_${field}').datetimepicker({format: "${param.format}", locale: "${pageContext.response.locale}"});
    });
</script>