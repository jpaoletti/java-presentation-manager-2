<span class="btn btn-default btn-sm fileinput-button pull-left">
    <i class="glyphicon glyphicon-upload"></i>
    <input name="file" id="field_${field}"  type="file" data-url="${cp}jpm/uploadFileConverter" ${param.multiple?'multiple=""':''} />
</span>
<input name="field_${field}" type="hidden" />
<div id="progress_${field}" class="progress"><div class="progress-bar"></div></div>
<script src="${cp}static/js/jquery.fileupload/jquery.ui.widget.js"></script>
<script src="${cp}static/js/jquery.fileupload/jquery.iframe-transport.js"></script>
<script src="${cp}static/js/jquery.fileupload/jquery.fileupload.js"></script>
<script>
    jpmLoad(function() {
        if (!$("link[href='${cp}static/css/jquery.fileupload-ui.css']").length) {
            $('<link href="${cp}static/css/jquery.fileupload-ui.css" rel="stylesheet">').appendTo("head");
        }
        $('#field_${field}').fileupload({
            dataType: 'json'
        }).on('fileuploadprogressall', function(e, data) {
            var progress = parseInt(data.loaded / data.total * 100, 10);
            $('#progress_${field} .progress-bar').css('width', progress + '%');
        }).on('fileuploaddone', function(e, data) {
            $.each(data.result.files, function(index, file) {
                $("input[name='field_${field}']").val(file.name);
            });
            $('#progress_${field} .progress-bar').addClass("progress-bar-success");
        }).on('fileuploadfail', function(e, data) {
            $.each(data.files, function(index, file) {
                alert("File upload failed");
            });
        })
    });
</script>