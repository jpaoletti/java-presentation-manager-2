<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<spring:message var="pwdShow" code="jpm.converter.password_converter.show" />
<spring:message var="pwdMask" code="jpm.converter.password_converter.mask" />
<spring:message var="pwdGenerate" code="jpm.converter.password_converter.generate" />
<spring:message var="pwdWeak" code="jpm.converter.password_converter.weak" />
<spring:message var="pwdMedium" code="jpm.converter.password_converter.medium" />
<spring:message var="pwdGood" code="jpm.converter.password_converter.good" />
<script type="text/javascript" src="${cp}static/js/pwd-converter.js?v=${jpm.appversion}"></script>
<div id="d_${field}" class="control-group">
    <div class="input-group">
        <input autocomplete="off" type="password" class="form-control"
               name="field_${field}" id="f_${field}" value="" />
        <button type="button" class="btn btn-outline-secondary"
                id="f_${field}_toggle"
                data-show-text="${pwdShow}" data-mask-text="${pwdMask}">
            <i class="fas fa-eye"></i> <span class="pwd-toggle-text">${pwdShow}</span>
        </button>
        <button type="button" class="btn btn-outline-secondary"
                id="f_${field}_generate">
            <i class="fas fa-key"></i> ${pwdGenerate}
        </button>
    </div>
    <div class="progress mt-1" style="height: 4px;">
        <div id="f_${field}_strength" class="progress-bar" role="progressbar"
             style="width: 0%;" aria-valuemin="0" aria-valuemax="100"></div>
    </div>
    <small id="f_${field}_strength_text" class="form-text text-muted"></small>
</div>
<script type="text/javascript">
    jpmLoad(function () {
        JpmPwdConverter.init({
            inputId: 'f_${field}',
            toggleId: 'f_${field}_toggle',
            generateId: 'f_${field}_generate',
            strengthBarId: 'f_${field}_strength',
            strengthTextId: 'f_${field}_strength_text',
            labels: {
                weak: "${pwdWeak}",
                medium: "${pwdMedium}",
                good: "${pwdGood}"
            }
        });
    });
</script>
