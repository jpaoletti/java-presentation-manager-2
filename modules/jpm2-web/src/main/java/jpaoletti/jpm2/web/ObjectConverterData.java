package jpaoletti.jpm2.web;

import java.util.List;

/**
 *
 * @author jpaoletti
 */
public class ObjectConverterData {

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
}