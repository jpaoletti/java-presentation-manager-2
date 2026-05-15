<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@attribute name = "paginatedList" required="true" type="jpaoletti.jpm2.core.model.PaginatedList" %>
<%@attribute name="i" required="true" type="java.lang.Integer"%>
<%@attribute name="extraQuery" required="false" type="java.lang.String"%>
<li class="page-item ${(paginatedList.page==i)?'active disabled':''}">
    <a href="?page=${i}${not empty extraQuery ? '&'.concat(extraQuery) : ''}" class="page-link ${(paginatedList.page==i)?'bg-info text-white':''}">${i}</a></li>
</li>
