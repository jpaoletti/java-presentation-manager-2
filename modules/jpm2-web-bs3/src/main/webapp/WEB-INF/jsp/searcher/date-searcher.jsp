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
        <div class="input-group">
            <input data-format="${param.format}" type='date' name="value" class="form-control" id="date-searcher"/>
            <span class="input-group-addon add-on"><i class="glyphicon glyphicon-calendar"></i></span>
        </div>
    </div>
</div>
