<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html lang="${locale.language}">
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <jpm:jpm-body>
        <jpm:jpm-item-operation>
            <div class="row">
                <div class="col-lg-8 col-md-10 col-sm-12">
                    <form class="form-horizontal" role="form" method="POST" id="jpmForm" autocomplete="off" action="${cp}jpm/sysparam/${id}/setValue.exec">
                        <div class="form-group row mb-3">
                            <label class="col-md-3 control-label"><spring:message code="jpm.field.sysparam.key" text="Key" /></label>
                            <div class="col-md-9">
                                <input type="text" class="form-control" value="${fn:escapeXml(key)}" readonly="readonly" />
                            </div>
                        </div>
                        <div class="form-group row mb-3">
                            <label class="col-md-3 control-label"><spring:message code="jpm.field.sysparam.value" text="Value" /></label>
                            <div class="col-md-9">
                                <c:choose>
                                    <%-- Secret: masked password, never prefilled --%>
                                    <c:when test="${secret}">
                                        <input type="password" name="value" id="sysparamValue" class="form-control" autocomplete="off" data-lpignore="true" data-1p-ignore data-form-type="other" placeholder="&bull;&bull;&bull;&bull;&bull;&bull;" />
                                        <small class="text-muted"><spring:message code="jpm.sysparam.secretHint" text="Leave blank to keep the current secret; type a new value to replace it." /></small>
                                    </c:when>
                                    <%-- Boolean: two radio options --%>
                                    <c:when test="${type == 'BOOLEAN'}">
                                        <div class="form-check">
                                            <input class="form-check-input" type="radio" name="value" id="sysparamValueTrue" value="true" ${currentValue == 'true' ? 'checked="checked"' : ''} />
                                            <label class="form-check-label" for="sysparamValueTrue"><spring:message code="jpm.sysparam.bool.true" text="True" /></label>
                                        </div>
                                        <div class="form-check">
                                            <input class="form-check-input" type="radio" name="value" id="sysparamValueFalse" value="false" ${currentValue != 'true' ? 'checked="checked"' : ''} />
                                            <label class="form-check-label" for="sysparamValueFalse"><spring:message code="jpm.sysparam.bool.false" text="False" /></label>
                                        </div>
                                    </c:when>
                                    <%-- Enum / constrained set of values: dropdown --%>
                                    <c:when test="${not empty allowedValues}">
                                        <select name="value" id="sysparamValue" class="form-select">
                                            <c:forEach var="opt" items="${allowedValues}">
                                                <option value="${fn:escapeXml(opt)}" ${currentValue == opt ? 'selected="selected"' : ''}>${fn:escapeXml(opt)}</option>
                                            </c:forEach>
                                        </select>
                                    </c:when>
                                    <%-- Integer-like numbers --%>
                                    <c:when test="${type == 'INTEGER' or type == 'LONG' or type == 'DURATION'}">
                                        <input type="number" step="1" name="value" id="sysparamValue" class="form-control" value="${fn:escapeXml(currentValue)}" />
                                    </c:when>
                                    <%-- Decimal numbers --%>
                                    <c:when test="${type == 'DECIMAL' or type == 'DOUBLE'}">
                                        <input type="number" step="any" name="value" id="sysparamValue" class="form-control" value="${fn:escapeXml(currentValue)}" />
                                    </c:when>
                                    <%-- Structured / multi-line text --%>
                                    <c:when test="${type == 'JSON' or type == 'LIST'}">
                                        <textarea name="value" id="sysparamValue" class="form-control" style="height: 160px;"><c:out value="${currentValue}" /></textarea>
                                    </c:when>
                                    <%-- Default: single-line text (STRING, URL, EMAIL, PATH, DATE, ...) --%>
                                    <c:otherwise>
                                        <input type="text" name="value" id="sysparamValue" class="form-control" value="${fn:escapeXml(currentValue)}" />
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                        <div class="form-group row">
                            <div class="col-md-9 offset-md-3">
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-save"></i> <spring:message code="jpm.button.save" text="Save" />
                                </button>
                                <c:if test="${not empty defaultValue}">
                                    <button type="button" class="btn btn-outline-secondary ms-2" id="sysparamRestoreDefault" data-default="${fn:escapeXml(defaultValue)}" title="<spring:message code="jpm.sysparam.restoreDefault.hint" text="Fill with the catalog default" />">
                                        <i class="fas fa-undo"></i> <spring:message code="jpm.sysparam.restoreDefault" text="Default" />
                                    </button>
                                </c:if>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </jpm:jpm-item-operation>
    </jpm:jpm-body>
    <script type="text/javascript">
        jpmLoad(function () {
            buildAjaxJpmForm();
            $('#sysparamRestoreDefault').on('click', function () {
                var d = String($(this).data('default'));
                var $radios = $('input[name=value][type=radio]');
                if ($radios.length) { $radios.prop('checked', false).filter('[value="' + d + '"]').prop('checked', true); return; }
                var $sel = $('select[name=value]');
                if ($sel.length) { $sel.val(d); return; }
                $('#sysparamValue').val(d);
            });
        });
    </script>
</html>
