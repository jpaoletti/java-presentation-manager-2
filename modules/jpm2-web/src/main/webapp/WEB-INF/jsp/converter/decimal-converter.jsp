<input class='form-control decimal' name='field_${field}' id="field_${field}" type='text' value='${param.value}'>
<script type="text/javascript" src="${cp}static/js/autoNumeric.js"></script>
<script type="text/javascript">
    jpmLoad(function() {
        var f = $('#field_${field}');
        f.autoNumeric('init', ${param.options});
        f.parents("form").submit(function() {
            var v = f.autoNumeric('get');
            f.autoNumeric('destroy');
            f.val(v);
            return true;
        });
    });
</script>