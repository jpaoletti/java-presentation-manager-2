<c:if test="${entity.paginable}">
    <ul class="pagination pagination-sm">
        <li ${(paginatedList.page > 1)?'':"class='disabled'"}><a href="?page=${paginatedList.page-1}">&laquo; </a></li>
        <c:if test="${entity.countable}">
            <c:if test="${paginatedList.pages > 20}">
                <jpm:pagination-link paginatedList='${paginatedList}' i="${1}" />
                <form action="" class="form-inline pull-right" role="form">
                    <div class="form-group">
                        <input class="form-control page-size input-sm" type="number" min="1" max="${paginatedList.pages}" value="${paginatedList.page}" name="page" style="width:60px;" />
                    </div>
                    <button type="submit" class="btn btn-default btn-sm"><span class="glyphicon glyphicon-arrow-right"></span></button>
                </form>
                <jpm:pagination-link paginatedList='${paginatedList}'  i="${paginatedList.pages}" />
            </c:if>
            <c:if test="${paginatedList.pages <= 20}">
                <c:forEach var="i" items="${paginatedList.pageRange}" >
                    <jpm:pagination-link paginatedList='${paginatedList}'  i="${i}" />
                </c:forEach>
            </c:if>
        </c:if>
        <c:if test="${not entity.countable}">
            <form action="" class="form-inline pull-right" role="form">
                <input class="form-control page-size input-sm" type="number" min="1" ${(paginatedList.more)?'':("max='".concat(paginatedList.page).concat("'"))} value="${paginatedList.page}" name="page" style="width:60px;" />
                <button type="submit" class="btn btn-default btn-sm"><span class="glyphicon glyphicon-arrow-right"></span></button>
            </form>
        </c:if>
        <li ${paginatedList.more?'':"class='disabled'"}>
            <a href="?page=${paginatedList.page+1}">&raquo;</a>
        </li>
    </ul>
</c:if>