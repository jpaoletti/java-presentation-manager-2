package jpaoletti.jpm2.core.menu;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import jpaoletti.jpm2.core.model.persistent.JpmMenuType;

/**
 * Item of the dynamic menu already filtered for the current user and ready to
 * render.
 *
 * @author jpaoletti
 */
public class JpmMenuNode implements Serializable {

    private final JpmMenuType type;
    private final String labelHtml;
    private final String icon;
    private final String href;
    private final boolean absolute;
    private final String htmlId;
    private final List<JpmMenuNode> children;

    public JpmMenuNode(JpmMenuType type, String labelHtml, String icon, String href, boolean absolute, String htmlId, List<JpmMenuNode> children) {
        this.type = type;
        this.labelHtml = labelHtml;
        this.icon = icon;
        this.href = href;
        this.absolute = absolute;
        this.htmlId = htmlId;
        this.children = children == null ? Collections.emptyList() : Collections.unmodifiableList(children);
    }

    public JpmMenuType getType() {
        return type;
    }

    /**
     * Label ready to print: a messages.properties text (trusted) or the escaped
     * literal label.
     */
    public String getLabelHtml() {
        return labelHtml;
    }

    public String getIcon() {
        return icon;
    }

    /**
     * Link of ENTITY and URL items: relative to the context path unless
     * {@link #isAbsolute()}.
     */
    public String getHref() {
        return href;
    }

    public boolean isAbsolute() {
        return absolute;
    }

    public String getHtmlId() {
        return htmlId;
    }

    public List<JpmMenuNode> getChildren() {
        return children;
    }

    public boolean isGroup() {
        return type == JpmMenuType.GROUP;
    }

    public boolean isLink() {
        return type.isLink();
    }

    public boolean isHeader() {
        return type == JpmMenuType.HEADER;
    }

    public boolean isSeparator() {
        return type == JpmMenuType.SEPARATOR;
    }
}
