<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:if test="${not empty globalMessage}">
    <div id="globalMessage" class="alert alert-${globalMessage.type.name} flyover flyover-top">
        <p>
        <spring:message code='${globalMessage.key}' text='${globalMessage.key}' arguments="${globalMessage.arguments}" />
        <c:set scope="session" var="globalMessage" value="" />
        </p>
    </div>
    <script type="text/javascript">
        jpmLoad(function () {
            $('#globalMessage').toggleClass('in');
            setTimeout(function () {
                $('#globalMessage').removeClass('in');
            }, 5000);
        });
    </script>
</c:if>
<c:if test="${jpm.cssMode !='css'}">
    <script type="text/javascript" src="${cp}static/js/less-1.4.1.min.js?v=${jpm.appversion}"></script>
</c:if>
<script type="text/javascript" src="${cp}static/js/bootstrap.min.js?v=${jpm.appversion}"></script>
<script type='text/javascript' src="${cp}static/js/bootstrap-dialog.js?v=${jpm.appversion}" ></script>
<script type="text/javascript">
        var contextPath = "${cp}";
        var localeLanguage = "${locale.language}";
        var currentUser = "${(user.getClass().name ne 'java.lang.String')?user.username:''}";
        var contextualEntity = "${contextualEntity}";
        //This initialize the internationalized javascript messages
        var messages = new Array();
        messages["jpm.modal.confirm.title"] = "<spring:message code='jpm.modal.confirm.title' text='Confirm' />";
        messages["jpm.modal.confirm.cancel"] = "<spring:message code='jpm.modal.confirm.cancel' text='Cancel' />";
        messages["jpm.modal.confirm.submit"] = "<spring:message code='jpm.modal.confirm.submit' text='Ok' />";
        messages["jpm.modal.confirm.text"] = "<spring:message code='jpm.modal.confirm.text' text='Are you sure you want to continue?' />";
        messages["jpm.modal.confirm.close"] = "<spring:message code='jpm.modal.confirm.close' text='Close' />";
        messages["jpm.usernav.addfavorite"] = "<spring:message code='jpm.usernav.addfavorite' text='' />";
        messages["jpm.addfavorite.popupTitle"] = "<spring:message code='jpm.addfavorite.popupTitle' text='' />";
        function getContextPath() {
            return location.protocol + '//' + location.hostname + (location.port ? ':' + location.port : '') + "${cp}";
        }
</script>
<script type="text/javascript" src="${cp}static/js/jquery.cookie.js?v=${jpm.appversion}"></script>
<script type="text/javascript" src="${cp}static/js/jpm.js?v=${jpm.appversion}"></script>
<script type="text/javascript" src="${cp}static/js/custom.js?v=${jpm.appversion}"></script>
<script type="text/javascript">
        jpmLoad(function () {
            $("#menu-${fn:replace(contextualEntity,'!', '-')}").addClass("active");
        });
</script>