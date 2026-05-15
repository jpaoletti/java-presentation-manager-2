package jpaoletti.jpm2.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.search.ISearchResult;
import jpaoletti.jpm2.core.search.ISearcher;
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
    private final List<SearchDefinition> searchDefinitions;
    private ListSort sort;

    public SessionEntityData(Entity entity, String context) throws PMException {
        this.entity = entity;
        this.pageSize = entity.getPageSize();
        this.searchCriteria = new SearchCriteria();
        this.searchDefinitions = new ArrayList<>();
        if (entity.getDefaultSearchs() != null) {
            for (SearchDefinition sd : entity.getDefaultSearchs()) {
                addSearchDefinition(sd, context);
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

    public List<SearchDefinition> getSearchDefinitions() {
        return searchDefinitions;
    }

    public boolean addSearchDefinition(SearchDefinition searchDefinition, String context) throws PMException {
        if (searchDefinition == null || searchDefinition.getFieldId() == null) {
            return false;
        }
        final Field field = entity.getFieldById(searchDefinition.getFieldId(), context);
        if (field == null) {
            throw new PMException(MessageFactory.error("jpm.field.not.found", searchDefinition.getFieldId()));
        }

        final Object searcher = field.getSearcher();
        if (searcher instanceof Searcher) {
            final Searcher.DescribedCriterion build = ((Searcher) searcher).build(entity, field, searchDefinition.getParametersForBuild());
            if (build != null) {
                searchCriteria.addDefinition(searchDefinition.getFieldId(), build);
                searchDefinitions.add(copySearchDefinition(searchDefinition));
                return true;
            }
        } else if (searcher instanceof ISearcher) {
            final ISearchResult result = ((ISearcher) searcher).build(entity, field, searchDefinition.getParametersForBuild());
            if (result != null) {
                searchCriteria.addSearchResult(searchDefinition.getFieldId(), result);
                searchDefinitions.add(copySearchDefinition(searchDefinition));
                return true;
            }
        }
        return false;
    }

    public void clearSearchDefinitions() {
        searchCriteria.clear();
        searchDefinitions.clear();
    }

    public void removeSearchDefinition(Integer index) {
        searchCriteria.removeDefinition(index);
        if (index != null && index >= 0 && index < searchDefinitions.size()) {
            searchDefinitions.remove(index.intValue());
        }
    }

    public void replaceSearchDefinitions(List<SearchDefinition> definitions, String context) throws PMException {
        clearSearchDefinitions();
        if (definitions != null) {
            for (SearchDefinition definition : definitions) {
                addSearchDefinition(definition, context);
            }
        }
    }

    public ListSort getSort() {
        return sort;
    }

    public void setSort(ListSort sort) {
        this.sort = sort;
    }

    private SearchDefinition copySearchDefinition(SearchDefinition searchDefinition) {
        final Map<String, List<String>> copiedParameters = new LinkedHashMap<>();
        if (searchDefinition.getParameters() != null) {
            for (Map.Entry<String, List<String>> entry : searchDefinition.getParameters().entrySet()) {
                copiedParameters.put(entry.getKey(), entry.getValue() == null ? new ArrayList<>() : new ArrayList<>(entry.getValue()));
            }
        }
        return new SearchDefinition(searchDefinition.getFieldId(), copiedParameters);
    }
}
