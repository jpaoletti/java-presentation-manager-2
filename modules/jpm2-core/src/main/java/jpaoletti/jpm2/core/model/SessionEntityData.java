package jpaoletti.jpm2.core.model;

import java.io.Serializable;
import jpaoletti.jpm2.core.search.Searcher;

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
        if (entity.getDefaultSearchs() != null) {
            for (SearchDefinition sd : entity.getDefaultSearchs()) {
                final Field field = entity.getFieldById(sd.getFieldId());
                final Searcher.DescribedCriterion build = field.getSearcher().build(field, sd.getParameters());
                searchCriteria.addDefinition(sd.getFieldId(), build);
            }
        }
        this.sort = new ListSort();
        if (entity.getDefaultSortField() != null) {
            this.sort.set(entity.getFieldById(entity.getDefaultSortField()));
            if (entity.getDefaultSortDirection() != null) {
                this.sort.setDirection(entity.getDefaultSortDirection());
            }
        }
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
