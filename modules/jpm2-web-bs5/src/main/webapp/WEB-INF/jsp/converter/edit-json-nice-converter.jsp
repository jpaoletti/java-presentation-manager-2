<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:if test="${empty jpmJsonEditorLoaded}">
    <c:set var="jpmJsonEditorLoaded" value="true" scope="request"/>
    <script src="${cp}static/js/jpm-json-editor.js"></script>
</c:if>
<div class="jpm-json-editor"
     data-ok-text="<spring:message code='jpm.converter.json.ok'/>"
     data-error-text="<spring:message code='jpm.converter.json.invalid'/>">
    <div class="jpm-json-toolbar">
        <button type="button" class="btn btn-sm btn-outline-secondary jpm-json-format">
            <spring:message code="jpm.converter.json.format"/>
        </button>
        <span class="jpm-json-status"></span>
    </div>
    <div class="jpm-json-body">
        <pre class="jpm-json-highlight" aria-hidden="true"><code></code></pre>
        <textarea class="jpm-json-input" id="field_${field}" name="field_${field}"
                  spellcheck="false" autocomplete="off" autocapitalize="off" wrap="off"><c:out value="${editJsonNiceValue}"/></textarea>
    </div>
</div>
<script type="text/javascript">
    jpmLoad(function () {
        if (window.jpmJsonEditor) {
            window.jpmJsonEditor.initAll();
        }
    });
</script>
