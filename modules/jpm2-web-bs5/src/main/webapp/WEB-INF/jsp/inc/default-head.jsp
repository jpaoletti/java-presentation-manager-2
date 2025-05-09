<c:catch var="exception"><c:set var="entityName" value="${entity.title}" /></c:catch>
<spring:message var="operationName" code="${operation.title}" arguments="${entityName}" text=""/>
<title>${jpm.title} ${not empty homeTitle?'- '.concat(homeTitle):''} ${not empty operationName?'- '.concat(operationName):''} ${instance.iobject.object}</title>
<meta charset="utf-8">
<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="mobile-web-app-capable" content="yes">
<meta name="description" content="">
<meta name="author" content="">
<meta name="version" content="${jpm.appversion}">
<link href="${cp}static/css/all.css?v=${jpm.appversion}" rel="stylesheet" type="text/css" />
<link href="${cp}static/css/tempus-dominus.min.css?v=${jpm.appversion}" rel="stylesheet" type="text/css" />

<script type='text/javascript' src="${cp}static/node_modules/jquery/dist/jquery.min.js?v=${jpm.appversion}" ></script>
<script type='text/javascript' src="${cp}static/node_modules/@popperjs/core/dist/umd/popper.js?v=${jpm.appversion}" ></script>
<script type='text/javascript' src="${cp}static/node_modules/jquery-blockui/jquery.blockUI.js?v=${jpm.appversion}" ></script>
<script type='text/javascript' src="${cp}static/node_modules/bootstrap/dist/js/bootstrap.min.js?v=${jpm.appversion}" ></script>
<script type='text/javascript' src="${cp}static/node_modules/jquery.cookie/jquery.cookie.js?v=${jpm.appversion}" ></script>
<!--<script type='text/javascript' src="${cp}static/node_modules/bootstrap5-dialog/dist/js/bootstrap-dialog.min.js?v=${jpm.appversion}" ></script>-->

<script type='text/javascript' src="${cp}static/js/jpm-head.js?v=${jpm.appversion}" ></script>
<script type="text/javascript" src="${cp}static/js/swiped-events.js?v=${jpm.appversion}"></script>
<script type="text/javascript" src="${cp}static/js/jquery.mCustomScrollbar.concat.min.js?v=${jpm.appversion}"></script>
<script type="text/javascript" src="${cp}static/js/jpm-dialogs.js?v=${jpm.appversion}"></script>
<script type="text/javascript" src="${cp}static/js/jpm.js?v=${jpm.appversion}"></script>
<script type="text/javascript" src="${cp}static/js/custom.js?v=${jpm.appversion}"></script>
<script type='text/javascript' src="${cp}static/js/jquery.jeditable.min.js?v=${jpm.appversion}" ></script>
<script type="text/javascript" src="${cp}static/js/jquery.form.min.js?v=${jpm.appversion}"></script>

<!--TODO Replace with local-->
<script type="text/javascript"  src="${cp}static/js/tempus-dominus.min.js?v=${jpm.appversion}"></script>
<script type="text/javascript" src="${cp}static/js/jpm-datepicker-v6.js?v=${jpm.appversion}"></script>

<script type="text/javascript" src="${cp}static/js/moment.min.js?v=${jpm.appversion}" charset="UTF-8"></script>
<script type="text/javascript" src="${cp}static/js/bootstrap-datetimepicker.min.js?v=${jpm.appversion}"></script>

<link rel="shortcut icon" href="${cp}static/favicon.ico?v=${jpm.appversion}">
<link rel="icon" type="image/png" sizes="150x150" href="${cp}static/favicon.ico?v=${jpm.appversion}">
<link rel="apple-touch-icon" sizes="150x150" href="${cp}static/favicon.ico?v=${jpm.appversion}">