package jpaoletti.jpm2.controller.ajax;

import static com.opensymphony.xwork2.Action.SUCCESS;
import java.util.ArrayList;
import java.util.List;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.util.JPMUtils;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

/**
 * Ajax getter for WebEditObject converter. Expented format:
 *
 * {results:[{id:1, text:'Red'},{id:2, text:'Blue'}], more:true}
 *
 * @author jpaoletti
 */
public class ObjectConverterGetData extends BaseAjaxAction {

    private String textField;
    private String query;
    private Integer page;
    private Integer pageSize;
    private String id;

    @Override
    public String execute() throws Exception {
        loadEntity();
        final Field field = getEntity().getFieldById(getTextField());
        if (getId() == null || "".equals(getId())) {
            final ObjectConverterData r = new ObjectConverterData();
            final List list = getEntity().getDao().list((getPage() - 1) * getPageSize(), getPageSize(), Restrictions.like(field.getProperty(), query, MatchMode.ANYWHERE));
            r.setMore(list.size() == getPageSize());
            r.setResults(new ArrayList<ObjectConverterDataItem>());
            for (Object object : list) {
                r.getResults().add(new ObjectConverterDataItem(
                        getEntity().getDao().getId(object).toString(),
                        JPMUtils.get(object, field.getProperty()).toString()));
            }
            setResult(r);
        } else {
            final Object object = getEntity().getDao().get(getId());
            setResult(new ObjectConverterDataItem(
                    getEntity().getDao().getId(object).toString(),
                    JPMUtils.get(object, field.getProperty()).toString()));
        }
        return SUCCESS;
    }

    public static class ObjectConverterData {

        private List<ObjectConverterDataItem> results;
        private boolean more;

        public List<ObjectConverterDataItem> getResults() {
            return results;
        }

        public void setResults(List<ObjectConverterDataItem> results) {
            this.results = results;
        }

        public boolean isMore() {
            return more;
        }

        public void setMore(boolean more) {
            this.more = more;
        }
    }

    public static class ObjectConverterDataItem {

        private String id;
        private String text;

        public ObjectConverterDataItem(String id, String text) {
            this.id = id;
            this.text = text;
        }

        public ObjectConverterDataItem() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Integer getPage() {
        if (page == null) {
            return 1;
        }
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        if (pageSize == null) {
            return 20;
        }
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getTextField() {
        return textField;
    }

    public void setTextField(String textField) {
        this.textField = textField;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
