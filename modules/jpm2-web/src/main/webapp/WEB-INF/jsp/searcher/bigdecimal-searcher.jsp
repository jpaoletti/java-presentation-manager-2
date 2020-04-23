<div class="row">
    <div class="col-lg-3">
        <select name='operator' class="form-control">
            <option value='eq'>&equals;</option>
            <option value='ne'>&lt;&gt;</option>
            <option value='<'>&lt;</option>
            <option value='>'>&gt;</option>
            <option value='<='>&le;</option>
            <option value='>='>&ge;</option>
        </select>
    </div>
    <div class="col-lg-9">
        <input class='form-control decimal' name='value' type='text' value="" id="bigdecimal-searcher" required="">
    </div>
</div>
<script type="text/javascript" src="${cp}static/js/autoNumeric-4.x.x.js?v=${jpm.appversion}"></script>
<script type="text/javascript">
    var bigDecimalSearcher;
    jpmLoad(function() {
        bigDecimalSearcher = new AutoNumeric('#bigdecimal-searcher', ${param.options});
    });
</script>
