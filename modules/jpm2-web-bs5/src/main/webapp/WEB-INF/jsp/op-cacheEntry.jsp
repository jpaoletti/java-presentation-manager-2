<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html lang="${locale.language}">
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <jpm:jpm-body>
        <jpm:jpm-item-operation>
            <div class="alert alert-info">
                Consulta y deposita manualmente el valor de una clave en la cache <code>${code}</code>.
                Escrib&iacute; una clave y pres&iacute; <b>Consultar</b> para ver su valor actual, o edit&aacute; el
                valor y pres&iacute; <b>Guardar</b> para depositarlo.
            </div>
            <div class="row">
                <div class="col-lg-8 col-md-10 col-sm-12">
                    <form class="form-horizontal" role="form" method="POST" id="jpmForm" action="${cp}jpm/cacheAdmin/${cacheId}/cacheEntry.exec">
                        <div class="form-group row">
                            <label class="col-md-2 control-label">Clave</label>
                            <div class="col-md-8">
                                <input type="text" name="key" id="cacheKey" class="form-control" autocomplete="off" value="${fn:escapeXml(selectedKey)}"/>
                            </div>
                            <div class="col-md-2">
                                <button type="button" id="btnQuery" class="btn btn-secondary w-100">
                                    <i class="fas fa-search"></i> Consultar
                                </button>
                            </div>
                        </div>
                        <c:if test="${queried and not found}">
                            <div class="form-group row">
                                <div class="col-md-8 offset-md-2">
                                    <span class="badge bg-warning text-dark">La clave no existe actualmente en la cache</span>
                                </div>
                            </div>
                        </c:if>
                        <div class="form-group row">
                            <label class="col-md-2 control-label">Valor</label>
                            <div class="col-md-8">
                                <textarea name="value" id="cacheValue" class="form-control" style="height: 200px;"><c:out value="${selectedValue}"/></textarea>
                            </div>
                        </div>
                        <div class="form-group row">
                            <div class="col-md-8 offset-md-2">
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-save"></i> Guardar
                                </button>
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
            $("#btnQuery").on("click", function () {
                var key = $("#cacheKey").val();
                if (!key || !key.trim()) {
                    jpmDialog({title: "Falta la clave", message: "Debe indicar una clave para consultar."});
                    return;
                }
                window.location.href = "${cp}jpm/cacheAdmin/${cacheId}/cacheEntry.exec?key=" + encodeURIComponent(key.trim());
            });
        });
    </script>
</html>
