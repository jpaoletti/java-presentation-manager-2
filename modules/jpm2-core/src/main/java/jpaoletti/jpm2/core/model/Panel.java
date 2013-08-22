package jpaoletti.jpm2.core.model;

import java.util.Arrays;
import java.util.List;

/**
 * A grouped set of fields.
 *
 * @author jpaoletti
 */
public class Panel {

    private String icon;
    private String title;
    private String fields;
    private Integer blocks;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * Fields contained in this panel.
     */
    public List<String> getFieldList() {
        return Arrays.asList(getFields().split("[ ]"));
    }

    public Integer getBlocks() {
        return blocks;
    }

    public void setBlocks(Integer blocks) {
        this.blocks = blocks;
    }
}
