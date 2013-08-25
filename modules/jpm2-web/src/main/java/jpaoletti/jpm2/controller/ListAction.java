package jpaoletti.jpm2.controller;

import static com.opensymphony.xwork2.Action.SUCCESS;
import java.util.HashMap;
import java.util.Map;
import jpaoletti.jpm2.core.model.EntityInstanceList;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.PaginatedList;
import org.hibernate.criterion.Criterion;

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
    private Map<Field, String> fieldSearchs;

    @Override
    public String execute() throws Exception {
        final String prepare = prepare();
        if (prepare.equals(SUCCESS)) {
            this.list = new EntityInstanceList();

            if (isPaginable()) {
                this.paginatedList = new PaginatedList();
                getPaginatedList().setPageSize(getPageSize());
                getPaginatedList().setPage(getPage());
                if (getSearch() == null) {
                    getList().load(getDao().list(getPaginatedList().from(), getPaginatedList().getPageSize()), getEntity(), getOperation());
                    getPaginatedList().setTotal(getDao().count());
                } else {
                    getList().load(getDao().list(getPaginatedList().from(), getPaginatedList().getPageSize(), getSearch()), getEntity(), getOperation());
                    getPaginatedList().setTotal(getDao().count(getSearch()));
                }
                getPaginatedList().setContents(getList());
                getSessionEntityData().setPage(getPaginatedList().getPage());
                getSessionEntityData().setPageSize(getPaginatedList().getPageSize());
            } else {
                if (getSearch() == null) {
                    getList().load(getDao().list(), getEntity(), getOperation());
                } else {
                    getList().load(getDao().list(getSearch()), getEntity(), getOperation());
                }
            }
            fieldSearchs = new HashMap<>();
            for (Field field : getEntity().getAllFields()) {
                if (field.getSearcher() != null) {
                    fieldSearchs.put(field, field.getSearcher().visualization());
                }
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

    public Map<Field, String> getFieldSearchs() {
        return fieldSearchs;
    }

    protected Criterion getSearch() {
        return getSessionEntityData().getSearchCriteria().getCriterion();
    }
}
