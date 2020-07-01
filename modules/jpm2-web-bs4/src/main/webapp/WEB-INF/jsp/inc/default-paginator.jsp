<c:if test="${entity.paginable}">
    <div class="inline-block float-left">
        <c:if test="${not entity.countable}">
            <form action="" class="form-inline d-flex flex-nowrap" role="form">
                <input class="form-control page-size form-control-sm" type="number" min="1" ${(paginatedList.more)?'':("max='".concat(paginatedList.page).concat("'"))} value="${paginatedList.page}" name="page" style="width:60px;" />
                <button type="submit" class="btn btn-primary btn-sm"><span class="fas fa-arrow-right"></span></button>
            </form>
        </c:if>
        <c:if test="${entity.countable and paginatedList.pages > 20}">
            <form action="" class="form-inline d-flex flex-nowrap" role="form">
                <div class="form-group">
                    <input class="form-control page-size form-control-sm" type="number" min="1" max="${paginatedList.pages}" value="${paginatedList.page}" name="page" style="width:60px;" />
                </div>
                <button type="submit" class="btn btn-secondary btn-sm"><span class="fas fa-arrow-right"></span></button>
            </form>
        </c:if>
        <ul class="pagination pagination-sm">
            <li class="page-item" ${(paginatedList.page > 1)?'':"class='disabled'"}><a href="?page=${paginatedList.page-1}" class="page-link">&laquo; </a></li>
            <c:if test="${entity.countable}">
                <c:if test="${paginatedList.pages > 20}">
                    <jpm:pagination-link paginatedList='${paginatedList}' i="${1}" />
                    <jpm:pagination-link paginatedList='${paginatedList}' i="${paginatedList.pages}" />
                </c:if>
                <c:if test="${paginatedList.pages <= 20}">
                    <c:forEach var="i" items="${paginatedList.pageRange}" >
                        <jpm:pagination-link paginatedList='${paginatedList}'  i="${i}" />
                    </c:forEach>
                </c:if>
            </c:if>
            <li class="page-item" ${paginatedList.more?'':"class='disabled'"}>
                <a href="?page=${paginatedList.page+1}" class="page-link">&raquo;</a>
            </li>
        </ul>
    </div>
    <div class="inline-block float-right">
        <form action="" class="form-inline d-flex flex-nowrap float-right" role="form">
            <input type="hidden" name="entityId" value="${entityId}" />
            <input type="hidden" name="page" value="${paginatedList.page}" />
            <div class="input-group mb-2 mr-sm-2">
                <div class="input-group-prepend">
                    <div class="input-group-text" title="<spring:message code="jpm.list.pagesize" text="Page size" />"><span class="fas fa-ruler-vertical"></span></div>
            </div>
            <input class="form-control form-control-sm page-size" type="number" min="0" max="100" value="${paginatedList.pageSize}" name="pageSize" />
    </div>
    <button type="submit" class="btn btn-primary btn-sm mb-2"><span class="fas fa-arrow-right"></span></button>
</form>
</div>
</c:if>