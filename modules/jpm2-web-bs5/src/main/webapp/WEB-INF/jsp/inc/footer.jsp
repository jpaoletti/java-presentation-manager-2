<footer class="footer mt-auto">
    <div class="container-fluid">
        <span class="text-muted">
            <small>
                <spring:message code="jpm.copyright" text="??" arguments="jpaoletti,2020" argumentSeparator="," /> 
                - v${jpm.appversion}
                - <a href="mailto:${jpm.contact}" ><spring:message code='jpm.contact' text='Contact' /> </a>
            </small>
            <small class="float-end">
                <i class="fa fa-user"></i>
                ${user.name}
            </small>
        </span>
    </div>
</footer>