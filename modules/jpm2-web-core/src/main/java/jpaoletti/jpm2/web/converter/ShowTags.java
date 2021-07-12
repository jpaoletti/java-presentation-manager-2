package jpaoletti.jpm2.web.converter;

import java.util.Collection;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author jpaoletti
 */
public class ShowTags extends Converter {

    @Autowired
    private JPMContext ctx;

    @Override
    public Object visualizeValue(ContextualEntity contextualEntity, Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final Collection<Tag> value = (Collection<Tag>) ((object == null) ? null : object);
        if (value == null || value.isEmpty()) {
            return "";
        } else {
            final StringBuilder sb = new StringBuilder("<div class='show-tags-converter'>");
            for (Tag o : value) {
                if (o.getOperations().contains(ctx.getOperation().getId())) {
                    sb.append("<span data-id='").append(o.getId()).append("' style='").append(o.getStyle()).append("' class='tag'>");
                    sb.append(o.getDescription());
                    sb.append("</span>&nbsp;");
                }
            }
            return sb.append("</div>").toString();
        }
    }

    public JPMContext getCtx() {
        return ctx;
    }

    public void setCtx(JPMContext ctx) {
        this.ctx = ctx;
    }
}

