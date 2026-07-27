<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html lang="${locale.language}">
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <jpm:jpm-body>
        <jpm:jpm-item-operation>
            <div class="row">
                <div class="col-lg-6">
                    <table class="table table-sm table-bordered w-100">
                        <thead>
                            <tr>
                                <th style="width: 300px;">Clave</th>
                                <th>Valor</th>
                            </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="entry" items="${info}">
                            <tr>
                                <td>
                                    ${entry.key}
                                </td>
                                <td>
                                    ${entry.value}
                                </td>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </jpm:jpm-item-operation>
    </jpm:jpm-body>
    <script type="text/javascript">
        jpmLoad(function () {
        });
    </script>
</html>
