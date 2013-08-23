package jpaoletti.jpm2.controller;

import static com.opensymphony.xwork2.Action.SUCCESS;
import jpaoletti.jpm2.core.model.EntityInstanceList;
import jpaoletti.jpm2.core.model.PaginatedList;

/**
 *
 * @author jpaoletti
 */
public class ListAction extends OperationAction {
    //Parameters

    private Integer page;
    private Integer pageSize;
    //Result
    private EntityInstanceList list;
    private PaginatedList paginatedList;

    @Override
    public String execute() throws Exception {
        final String prepare = prepare();
        if (prepare.equals(SUCCESS)) {
            this.list = new EntityInstanceList();

            if (isPaginable()) {
                this.paginatedList = new PaginatedList();
                getPaginatedList().setPageSize(getPageSize());
                getPaginatedList().setPage(getPage());
                getPaginatedList().setTotal(getDao().count());
                getList().load(getDao().list(getPaginatedList().from(), getPaginatedList().getPageSize()), getEntity(), getOperation());
                getPaginatedList().setContents(getList());

                getSessionEntityData().setPage(getPaginatedList().getPage());
                getSessionEntityData().setPageSize(getPaginatedList().getPageSize());
            } else {
                getList().load(getDao().list(), getEntity(), getOperation());
            }
            return SUCCESS;
        } else {
            return prepare;
        }
    }

    public EntityInstanceList getList() {
        return list;
    }

    public void setList(EntityInstanceList list) {
        this.list = list;
    }

    public Integer getPage() {
        if (page == null) {
            return getSessionEntityData().getPage();
        } else {
            return page;
        }
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        if (pageSize == null) {
            return getSessionEntityData().getPageSize();
        } else {
            return pageSize;
        }
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public PaginatedList getPaginatedList() {
        return paginatedList;
    }

    public boolean isPaginable() {
        return getOperation().getConfig("paginable", "true").equalsIgnoreCase("true");
    }
}
