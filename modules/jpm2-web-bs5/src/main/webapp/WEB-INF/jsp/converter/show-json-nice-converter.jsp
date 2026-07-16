<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${not empty wsjsonc_pretty}">
    <pre class="jpm-json-nice"><code class="language-json"><c:out value="${wsjsonc_pretty}"/></code></pre>
</c:if>
