package jpaoletti.jpm2.core.model;

import java.io.Serializable;

/**
 *
 * @author jpaoletti
 */
public class SessionEntityData implements Serializable {

    private String entityId;
    private Integer page;
    private Integer pageSize;
    private SearchCriteria searchCriteria;
    private ListSort sort;

    public SessionEntityData(String entityId) {
        this.entityId = entityId;
        this.searchCriteria = new SearchCriteria();
        this.sort = new ListSort();
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public SearchCriteria getSearchCriteria() {
        return searchCriteria;
    }

    public ListSort getSort() {
        return sort;
    }

    public void setSort(ListSort sort) {
        this.sort = sort;
    }
}
