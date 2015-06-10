<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<script type="text/javascript" src="${cp}static/js/pwdwidget.js?v=${jpm.appversion}"></script>
<div id="d_${field}" class="control-group">
    <div class='pwdwidgetdiv' id='thepwddiv${field}'></div>
</div>
<script type="text/javascript" >
    jpmLoad(function() {
        var pwdwidget = new PasswordWidget('thepwddiv${field}', 'field_${field}');
        pwdwidget.txtShow = "<spring:message code='jpm.converter.password_converter.show' />";
        pwdwidget.txtMask = "<spring:message code='jpm.converter.password_converter.mask' />";
        pwdwidget.txtGenerate = "<spring:message code='jpm.converter.password_converter.generate' />";
        pwdwidget.txtWeak = "<spring:message code='jpm.converter.password_converter.weak' />";
        pwdwidget.txtMedium = "<spring:message code='jpm.converter.password_converter.medium' />";
        pwdwidget.txtGood = "<spring:message code='jpm.converter.password_converter.good' />";
        pwdwidget.MakePWDWidget();
        $("#d_${field}").parents("form").attr("autocomplete", "off");
    });
</script>