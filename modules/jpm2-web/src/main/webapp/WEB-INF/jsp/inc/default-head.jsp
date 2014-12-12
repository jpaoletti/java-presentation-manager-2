<c:set var="entityName" value="${entity.title}" />
<spring:message var="operationName" code="${operation.title}" arguments="${entityName}" text=""/>
<title>${jpm.title} ${not empty homeTitle?'- '.concat(homeTitle):''} ${not empty operationName?'- '.concat(operationName):''} ${instance.iobject.object}</title>
<meta charset="utf-8">
<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">
<c:if test="${jpm.cssMode=='css'}">
    <link href="${cp}static/css/all.css?v=${jpm.appversion}" rel="stylesheet" type="text/css" />
</c:if>
<c:if test="${jpm.cssMode !='css'}">
    <link href="${cp}static/less/all.less?v=${jpm.appversion}" rel="stylesheet" type="text/less"  />
</c:if>
<script type='text/javascript' src="${cp}static/js/jquery-2.1.1.min.js?v=${jpm.appversion}" ></script>
<script type='text/javascript' src="${cp}static/js/jpm-head.js?v=${jpm.appversion}" ></script>
<link rel="shortcut icon" href="${cp}static/favicon.ico">