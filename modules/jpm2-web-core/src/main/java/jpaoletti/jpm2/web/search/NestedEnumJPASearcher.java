package jpaoletti.jpm2.web.search;

import java.util.List;
import java.util.Map;
import javax.persistence.criteria.Path;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.search.ISearchResult;
import jpaoletti.jpm2.core.search.JPASearchResult;

/**
 * JPA Criteria API implementation of {@link NestedEnumSearcher}.
 *
 * {@link EnumJPASearcher} that supports nested properties (e.g.
 * {@code source.participantType}). The nested path is navigated with chained
 * {@code get()} calls over the root, which create the necessary implicit joins
 * (so no explicit aliases are required).
 *
 * The enum class can be forced through {@code enumClassName} when the property
 * type cannot be resolved from the entity (typically for nested properties).
 *
 * @author jpaoletti
 */
public class NestedEnumJPASearcher extends EnumJPASearcher {

    private String enumClassName;

    @Override
    public ISearchResult build(Entity entity, Field field, Map<String, String[]> parameters) {
        final List<Object> values = getValues(entity, field, parameters);
        final String[] path = field.getProperty().split("[.]");
        return new JPASearchResult(
                MessageFactory.info(DESCRIPTION_KEY, String.valueOf(values)),
                (cb, root) -> {
                    Path p = root;
                    for (String part : path) {
                        p = p.get(part);
                    }
                    return p.in(values);
                });
    }

    @Override
    protected Class getEnumClass(Entity entity, Field field) {
        if (enumClassName != null) {
            try {
                return Class.forName(enumClassName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Enum class not found: " + enumClassName, e);
            }
        }
        return super.getEnumClass(entity, field);
    }

    public String getEnumClassName() {
        return enumClassName;
    }

    public void setEnumClassName(String enumClassName) {
        this.enumClassName = enumClassName;
    }
}
