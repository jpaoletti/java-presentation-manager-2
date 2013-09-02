<c:if test="${jpm.cssMode !='css'}">
    <script type="text/javascript" src="${cp}static/js/less-1.4.1.min.js?v=${jpm.appversion}"></script>
</c:if>
<script type="text/javascript" src="${cp}static/js/bootstrap.min.js?v=${jpm.appversion}"></script>
<script type="text/javascript" src="${cp}static/js/jquery.jpanelmenu.min.js?v=${jpm.appversion}"></script>
<script type="text/javascript">
    //This initialize the internationalized javascript messages
    var messages = new Array();
    messages["jpm.modal.confirm.title"] = "<spring:message code='jpm.modal.confirm.title' text='Confirm' />";
    messages["jpm.modal.confirm.cancel"] = "<spring:message code='jpm.modal.confirm.cancel' text='Cancel' />";
    messages["jpm.modal.confirm.submit"] = "<spring:message code='jpm.modal.confirm.submit' text='Ok' />";
    messages["jpm.modal.confirm.text"] = "<spring:message code='jpm.modal.confirm.text' text='Are you sure you want to continue?' />";
</script>
<script type="text/javascript" src="${cp}static/js/jpm.js?v=${jpm.appversion}"></script>