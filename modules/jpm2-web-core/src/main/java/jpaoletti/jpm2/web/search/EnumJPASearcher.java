package jpaoletti.jpm2.web.search;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.search.ISearchResult;
import jpaoletti.jpm2.core.search.ISearcher;
import jpaoletti.jpm2.core.search.JPASearchResult;
import jpaoletti.jpm2.core.search.jpa.JPASearcherHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * JPA Criteria API implementation of {@link EnumSearcher}.
 *
 * Same visualization and value parsing as {@link EnumSearcher}, but builds a
 * {@link JPASearchResult} (predicate) instead of a Hibernate Criterion, so it
 * works with JPA-based DAOs ({@code JPADAO}).
 *
 * @author jpaoletti
 */
@Component
public class EnumJPASearcher implements ISearcher {

    public static final String DESCRIPTION_KEY = "jpm.searcher.enumSearcher";

    @Override
    public String visualization(Entity entity, Field field) {
        try {
            final Class enumClass = getEnumClass(entity, field);
            final List<String> options = new ArrayList<>();
            for (Object option : EnumSet.allOf(enumClass)) {
                options.add(((Enum) option).name() + "@" + String.valueOf(option));
            }
            final StringBuilder sb = new StringBuilder("@page:enum-searcher.jsp?options=" + URLEncoder.encode(StringUtils.join(options, ","), "UTF-8"));
            return sb.toString();
        } catch (UnsupportedEncodingException ex) {
            return "";
        }
    }

    @Override
    public ISearchResult build(Entity entity, Field field, Map<String, String[]> parameters) {
        final List<Object> values = getValues(entity, field, parameters);
        final String searchProperty = JPASearcherHelper.getSearchProperty(field);
        final JPASearchResult result = new JPASearchResult(
                MessageFactory.info(DESCRIPTION_KEY, String.valueOf(values)),
                (cb, root) -> root.get(searchProperty).in(values));
        return JPASearcherHelper.addAliases(result, field);
    }

    protected List<Object> getValues(Entity entity, Field field, Map<String, String[]> parameters) throws NumberFormatException {
        final String[] values = parameters.get("value");
        final List<Object> result = new ArrayList<>();
        for (String value : values) {
            final Class enumClass = getEnumClass(entity, field);
            result.add(valueOf(enumClass, value));
        }
        return result;
    }

    protected Class getEnumClass(Entity entity, Field field) {
        return field.getClass(entity.getClazz());
    }

    protected Enum valueOf(final Class enumClass, Object newValue) {
        return Enum.valueOf(enumClass, String.valueOf(newValue));
    }

}
