<%@tag description="Default body" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@attribute name = "showMenu" required="false" type="java.lang.Boolean" %>
<%@attribute name = "showHeader" required="false" type="java.lang.Boolean" %>
<body lang="${locale.language}">
    <div id="wrap">
        <c:if test="${(empty showHeader) or showHeader}">
            <%@include file="../jsp/inc/header.jsp" %>
        </c:if>
        <div class="container">
            <div class="row">
                <c:catch var="e">
                    <c:if test="${(empty showMenu) or showMenu}">
                        <div class="col-md-2 hidden-xs hidden-sm">
                            <div class="bs-sidebar hidden-print affix">
                                <jsp:include page="../jsp/inc/menu/${currentHome}-menu.jsp" />
                            </div>
                        </div>
                    </c:if>
                </c:catch>
                <div class="${(((empty showMenu) or showMenu) and empty e)?'col-md-10 col-xs-12 col-sm-12':'col-md-12'}">
                    <jsp:doBody />
                </div>
            </div>
        </div>
    </div>
    <%@include  file="../jsp/inc/footer.jsp" %>
    <%@include  file="../jsp/inc/default-javascript.jsp" %>
</body>