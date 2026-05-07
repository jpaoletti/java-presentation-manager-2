<link rel="stylesheet" href="${cp}static/node_modules/trumbowyg/dist/ui/trumbowyg.min.css"/>
<style type="text/css">
    .jpm-rich-html-editor + .trumbowyg-box,
    .jpm-rich-html-editor + .trumbowyg-box .trumbowyg-editor {
        min-height: 220px;
    }

    .jpm-rich-html-editor + .trumbowyg-box {
        border-radius: .5rem;
        border-color: var(--bs-border-color, #dee2e6);
    }

    .jpm-rich-html-editor + .trumbowyg-box .trumbowyg-button-pane {
        background: var(--bs-tertiary-bg, #f8f9fa);
        border-bottom-color: var(--bs-border-color, #dee2e6);
    }

    .jpm-rich-html-editor + .trumbowyg-box.trumbowyg-editor-visible {
        box-shadow: 0 0 0 .2rem rgba(13, 110, 253, .15);
        border-color: rgba(13, 110, 253, .35);
    }
    
    .jpm-rich-html-editor + .trumbowyg-box .trumbowyg-editor {
        font-size: .95rem;
        line-height: 1.5;
    }
</style>
<script src="${cp}static/node_modules/trumbowyg/dist/trumbowyg.min.js"></script>
<textarea class="form-control jpm-rich-html-editor" cols="100" rows="12" id="field_${field}" name="field_${field}">${htmlRichConverterValue}</textarea>
<script type="text/javascript">
    jpmLoad(function () {
        $.trumbowyg.svgPath = '${cp}static/node_modules/trumbowyg/dist/ui/icons.svg';
        $('#field_${field}').trumbowyg({
            autogrow: true,
            removeformatPasted: true,
            btns: [
                ['undo', 'redo'],
                ['formatting'],
                ['strong', 'em', 'del'],
                ['link'],
                ['unorderedList', 'orderedList'],
                ['justifyLeft', 'justifyCenter', 'justifyRight'],
                ['horizontalRule'],
                ['viewHTML']
            ]
        });
    });
</script>
