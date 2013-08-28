    <title>${jpm.title}</title>
    <meta charset="utf-8">
    <meta http-equiv="content-type" content="text/html; charset=UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <c:if test="${jpm.cssMode=='css'}">
        <link href="<c:url value='/static/css/all.css?v=${jpm.appversion}' />" rel="stylesheet" type="text/css" />
    </c:if>
    <c:if test="${jpm.cssMode !='css'}">
        <link href="<c:url value='/static/less/all.less?v=${jpm.appversion}' />" rel="stylesheet" type="text/less"  />
    </c:if>
    <script type='text/javascript' src="<c:url value='/static/js/jquery-2.0.3.min.js?v=${jpm.appversion}' />" ></script>
    <script type='text/javascript' src="<c:url value='/static/js/jpm-head.js?v=${jpm.appversion}' />" ></script>
