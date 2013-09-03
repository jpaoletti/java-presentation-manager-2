package jpaoletti.jpm2.core.model;

import java.io.Serializable;

/**
 *
 * @author jpaoletti
 */
public class SessionEntityData implements Serializable {

    private Entity entity;
    private Integer page;
    private Integer pageSize;
    private SearchCriteria searchCriteria;
    private ListSort sort;

    public SessionEntityData(Entity entity) {
        this.entity = entity;
        this.searchCriteria = new SearchCriteria();
        this.sort = new ListSort();
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
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
