<div id="field_${field}" class="input-group">
    <input type="text" name="field_${field}" class="form-control datetimepicker-input" value="${param.value}" />
    <span class="input-group-text"><i class="fa fa-calendar-alt"></i></span>
</div>
<script type="text/javascript">
    jpmLoad(function () {
        const input = document.querySelector('[name="field_${field}"]');
        initTempusDominusField(input, "${param.format}", safeLocale("${pageContext.response.locale}"));
    });
</script>
