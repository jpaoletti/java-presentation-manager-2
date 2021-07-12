<input class='form-control decimal' name='field_${field}' id="field_${field}" type='text'>
<script type="text/javascript" src="${cp}static/js/autoNumeric-4.x.x.js?v=${jpm.appversion}"></script>
<script type="text/javascript">
    var autoNumericField_${field};
    jpmLoad(function () {
        autoNumericField_${field} = new AutoNumeric('#field_${field}', ${param.options}).set(${param.value});
    });
</script>