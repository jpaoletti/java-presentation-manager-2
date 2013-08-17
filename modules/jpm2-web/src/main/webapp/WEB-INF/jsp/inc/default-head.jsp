    <title>${pm.title}</title>
    <meta charset="utf-8">
    <meta http-equiv="content-type" content="text/html; charset=UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <c:if test="${pm.cssMode=='css'}">
        <link href="static/css/all.css?v=${pm.appversion}" rel="stylesheet" type="text/css" />
    </c:if>
    <c:if test="${pm.cssMode !='css'}">
        <link href="static/less/all.less?v=${pm.appversion}" rel="stylesheet" type="text/less"  />
    </c:if>