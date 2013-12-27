<input class='form-control decimal' name='field_${field}' id="field_${field}" type='text' value='${param.value}'>
<script type="text/javascript" src="${cp}static/js/autoNumeric.js"></script>
<script type="text/javascript">
    jpmLoad(function() {
        $('#field_${field}').autoNumeric('init', ${param.options});
    });
</script>