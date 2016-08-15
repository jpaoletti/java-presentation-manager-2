package jpaoletti.jpm2.core.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This list represents a list with a paged representation.
 *
 * @author jpaoletti
 */
public class PaginatedList {

    public static final int DEFAULT_PAGE_SIZE = 10;
    private EntityInstanceList contents;
    private Map<Field, String> fieldSearchs;
    private Integer page;
    private Integer pages;
    private Integer pageSize;
    private Long total;
    private ListFilter listFilter; //Permanent list filter
    private boolean paginable;
    private boolean hasSelectedScope;

    /**
     * Default constructor
     */
    public PaginatedList() {
        this.page = 1;
        this.pageSize = 10;
        this.contents = new EntityInstanceList();
        this.fieldSearchs = new LinkedHashMap<>();
    }

    /**
     * Constructor with contents and total
     *
     * @param contents
     * @param total
     */
    public PaginatedList(EntityInstanceList contents, Long total) {
        super();
        this.contents = contents;
        pageSize = DEFAULT_PAGE_SIZE; //Default
        this.page = 1;
        if (total != null) {
            setTotal(total);
        }
    }

    /**
     * Returns a list with the existing pages index
     *
     * @return
     */
    public List<Integer> getPageRange() {
        List<Integer> r = new ArrayList<>();
        for (int i = 1; i <= getPages(); i++) {
            r.add(i);
        }
        return r;
    }

    /**
     *
     * @param pageSize
     */
    public void setPageSize(Integer pageSize) {
        if (pageSize != null) {
            if (!Objects.equals(pageSize, getPageSize())) {
                this.page = 1;
            }
            this.pageSize = pageSize;
            setTotal(total);
        }
    }

    public EntityInstanceList getContents() {
        return contents;
    }

    /**
     *
     * @param contents
     */
    public void setContents(EntityInstanceList contents) {
        this.contents = contents;
    }

    /**
     *
     * @return
     */
    public Integer getPage() {
        if (page == null) {
            return 1;
        }
        return page;
    }

    /**
     *
     * @param page
     */
    public void setPage(Integer page) {
        if (page == null) {
            this.page = 1;
        } else if (getTotal() != null && page > getPages()) { //Out of range page sets to last one
            this.page = getPages();
        } else {
            this.page = page;
        }
    }

    /**
     *
     * @return
     */
    public Integer getPages() {
        if (pages == null) {
            return 1;
        }
        return pages;
    }

    /**
     *
     * @param pages
     */
    public void setPages(Integer pages) {
        this.pages = pages;
    }

    /**
     *
     * @return
     */
    public Long getTotal() {
        return total;
    }

    /**
     *
     * @param total
     */
    public final void setTotal(Long total) {
        this.total = total;
        if (total != null) {
            if (total % pageSize == 0) {
                this.pages = (int) (total / pageSize);
            } else {
                this.pages = (int) (total / pageSize) + 1;
            }
        }
    }

    public Integer getPageSize() {
        return pageSize == null ? DEFAULT_PAGE_SIZE : pageSize;
    }

    /**
     * Returns the starting index of the list
     *
     * @return
     */
    public Integer from() {
        return (this.getPage() != null) ? (((getPage() - 1) * getPageSize())) : 0;
    }

    /**
     * @param paginable the paginable to set
     */
    public void setPaginable(boolean paginable) {
        this.paginable = paginable;
    }

    /**
     * @return the paginable
     */
    public boolean isPaginable() {
        return paginable;
    }

    /**
     * @param hasSelectedScope the hasSelectedScope to set
     */
    public void setHasSelectedScope(boolean hasSelectedScope) {
        this.hasSelectedScope = hasSelectedScope;
    }

    /**
     * @return the hasSelectedScope
     */
    public boolean isHasSelectedScope() {
        return hasSelectedScope;
    }

    public Integer getListTotalDigits() {
        try {
            return (getTotal() == null || getTotal() == 0) ? 1 : (int) Math.log10(getTotal()) + 1;
        } catch (Exception ex) {
            return 0;
        }
    }

    public ListFilter getListFilter() {
        return listFilter;
    }

    public void setListFilter(ListFilter listFilter) {
        this.listFilter = listFilter;
    }

    /**
     * Next page. null if there is no such.
     */
    public Integer getNext() {
        if (getTotal() == null || getPage() < getPages()) {
            return getPage() + 1;
        } else {
            return null;
        }
    }

    /**
     * Previous page. null if there is no such.
     */
    public Integer getPrev() {
        if (getTotal() == null || getPage() > 1) {
            return getPage() - 1;
        } else {
            return null;
        }
    }

    public List<Field> getFields() {
        return getContents().getFields();
    }

    public Map<Field, String> getFieldSearchs() {
        return fieldSearchs;
    }

    public void setFieldSearchs(Map<Field, String> fieldSearchs) {
        this.fieldSearchs = fieldSearchs;
    }

    public boolean isMore() {
        return (total == null && getContents().size() == getPageSize()) || (total != null && getPage() < getPages());
    }
}
