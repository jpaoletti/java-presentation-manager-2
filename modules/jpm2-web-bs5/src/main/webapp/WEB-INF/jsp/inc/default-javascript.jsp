<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:if test="${not empty globalMessage}">
    <div id="globalMessage" class="alert alert-${globalMessage.type.name} flyover flyover-top">
        <p>
        <spring:message code='${globalMessage.key}' text='${globalMessage.key}' arguments="${globalMessage.arguments}" argumentSeparator=";" />
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
    messages["jpm.usernav.addfavorite"] = "<spring:message code='jpm.usernav.addfavorite' text='Add Fav' />";
    messages["jpm.usernav.removefavorite"] = "<spring:message code='jpm.usernav.removefavorite' text='Remove Fav' />";
    messages["jpm.addfavorite.popupTitle"] = "<spring:message code='jpm.addfavorite.popupTitle' text='' />";
    messages["jpm.modal.attachment.preview"] = "<spring:message code='jpm.modal.attachment.preview' text='' />";
    messages["jpm.modal.attachment.title"] = "<spring:message code='jpm.modal.attachment.title' text='' />";
    messages["jpm.modal.attachment.download"] = "<spring:message code='jpm.modal.attachment.download' text='' />";
    function getContextPath() {
        return location.protocol + '//' + location.hostname + (location.port ? ':' + location.port : '') + "${cp}";
    }
    var ROLE_USER_FAVORITE = false;
</script>
<security:authorize access="hasAnyRole('ROLE_USER_FAVORITE')">
    <script type="text/javascript">
        ROLE_USER_FAVORITE = true;
    </script>
</security:authorize>
<script type="text/javascript">
    jpmLoad(function () {
        $("#menu-${fn:replace(contextualEntity,'!', '-')}").addClass("active");
    });
</script>