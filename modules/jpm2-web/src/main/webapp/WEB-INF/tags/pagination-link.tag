<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@attribute name = "paginatedList" required="true" type="jpaoletti.jpm2.core.model.PaginatedList" %>
<%@attribute name="i" required="true" type="java.lang.Integer"%>
<li class="${(paginatedList.page==i)?'active disabled':''}" ><a href="?page=${i}">${i}</a></li>