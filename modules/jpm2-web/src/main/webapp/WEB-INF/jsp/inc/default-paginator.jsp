<c:if test="${paginable}">
    <div class="col-lg-12">
        <ul class="pagination pagination-sm">
            <li ${(paginatedList.page > 1)?'':"class='disabled'"}><a href="list?entityId=${entityId}&page=${paginatedList.page-1}">&laquo; </a></li>
            <c:if test="${paginatedList.total != null}">
                <c:if test="${paginatedList.pages > 20}">
                    <jpm:pagination-link paginatedList='${paginatedList}' i="${1}" />
                    <input name="page" value="${paginatedList.page}" id="page" size="5" type="number" />
                    <jpm:pagination-link paginatedList='${paginatedList}'  i="${paginatedList.pages}" />
                </c:if>
                <c:if test="${paginatedList.pages <= 20}">
                    <input type="hidden" value="${paginatedList.page}" id="page" name="page"/>
                    <c:forEach var="i" items="${paginatedList.pageRange}" >
                        <jpm:pagination-link paginatedList='${paginatedList}'  i="${i}" />
                    </c:forEach>
                </c:if>
            </c:if>
            <c:if test="${empty paginatedList.total}">
                <jpm:pagination-link paginatedList='${paginatedList}' i="${1}" />
                <input name="page" value="${paginatedList.page}" id="page" size="5" type="text" />
                <input type="hidden" value="${paginatedList.page}" id="page" name="page"/>
            </c:if>
            <li ${(empty paginatedList.total || paginatedList.page < paginatedList.pages)?'':"class='disabled'"}>
                <a href="list?entityId=${entityId}&page=${paginatedList.page+1}">&raquo;</a>
            </li>

        </ul>
    </div>
</c:if>