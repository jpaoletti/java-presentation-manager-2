<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@attribute name = "paginatedList" required="true" type="jpaoletti.jpm2.core.model.PaginatedList" %>
<%@attribute name="i" required="true" type="java.lang.Integer"%>
<a href="?page=${i}" class="btn btn-sm btn-outline-secondary ${(paginatedList.page==i)?'bg-info text-white active disabled':''}">${i}</a></li>