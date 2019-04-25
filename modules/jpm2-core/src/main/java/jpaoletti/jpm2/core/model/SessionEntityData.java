package jpaoletti.jpm2.core.model;

import java.io.Serializable;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.search.Searcher;

/**
 *
 * @author jpaoletti
 */
public class SessionEntityData implements Serializable {

    private Entity entity;
    private Integer page;
    private Integer pageSize;
    private final SearchCriteria searchCriteria;
    private ListSort sort;

    public SessionEntityData(Entity entity, String context) throws PMException {
        this.entity = entity;
        this.pageSize = entity.getPageSize();
        this.searchCriteria = new SearchCriteria();
        if (entity.getDefaultSearchs() != null) {
            for (SearchDefinition sd : entity.getDefaultSearchs()) {
                final Field field = entity.getFieldById(sd.getFieldId(), context);
                if (field == null) {
                    throw new PMException(MessageFactory.error("jpm.field.not.found", sd.getFieldId()));
                }
                final Searcher.DescribedCriterion build = field.getSearcher().build(field, sd.getParameters());
                if (build != null) {
                    searchCriteria.addDefinition(sd.getFieldId(), build);
                }
            }
        }
        this.sort = new ListSort();
        if (entity.getDefaultSortField(context) != null) {
            this.sort.set(entity.getFieldById(entity.getDefaultSortField(context), context));
            if (entity.getDefaultSortDirection(context) != null) {
                this.sort.setDirection(entity.getDefaultSortDirection(context));
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
