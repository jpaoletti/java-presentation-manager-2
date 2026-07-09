<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html lang="${locale.language}">
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <jpm:jpm-body>
        <jpm:jpm-item-operation>
            <form class="form-horizontal" role="form" method="POST" id="jpmForm">
                <div class="row jpm-content-panels">
                    <div class="col-md-6">
                        <fieldset>
                            <div class="form-group row mb-3">
                                <label for="to" class="col-sm-3 col-form-label">Email destino</label>
                                <div class="col-sm-9">
                                    <input type="email" name="to" id="to" class="form-control" placeholder="usuario@ejemplo.com" required />
                                </div>
                            </div>
                            <div class="col-lg-1">
                                <button type="submit" class="btn btn-primary">Enviar</button>
                            </div>
                        </fieldset>
                    </div>
                </div>
            </form>
        </jpm:jpm-item-operation>
    </jpm:jpm-body>
    <script type="text/javascript">
        jpmLoad(function () {
            buildAjaxJpmForm();
        });
    </script>
</html>
