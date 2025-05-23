<div id="field_${field}" class="input-group">
    <input type="text" name="field_${field}" class="form-control datetimepicker-input" value="${param.value}" />
    <span class="input-group-text"><i class="fa fa-calendar-alt"></i></span>
</div>
<script type="text/javascript" src="${cp}static/js/moment.min.js?v=${jpm.appversion}" charset="UTF-8"></script>
<script type="text/javascript" src="${cp}static/js/bootstrap-datetimepicker.min.js?v=${jpm.appversion}"></script>
<script type="text/javascript">
    jpmLoad(function () {
        $('[name="field_${field}"]')
                .on('focus', function () {
                    $(this).select();
                })
                .datetimepicker({format: "${param.format}", locale: safeLocale("${pageContext.response.locale}")});
    });
</script>