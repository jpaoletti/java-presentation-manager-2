package jpaoletti.jpm2.core.search.jpa;

import javax.persistence.criteria.JoinType;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.search.JPASearchResult;

/**
 * Helper class for JPA-based searchers.
 * Similar to SearcherHelper but works with JPASearchResult.
 *
 * @author jpaoletti
 */
public class JPASearcherHelper {

    /**
     * Adds necessary aliases for nested property searches
     *
     * @param searchResult the JPA search result
     * @param field the field being searched
     * @return the search result with aliases added
     */
    public static JPASearchResult addAliases(JPASearchResult searchResult, Field field) {
        if (field.getProperty().contains(".")) {
            final String[] split = field.getProperty().split("[.]");
            for (int i = 0; i < split.length - 1; i++) {
                final String alias = split[i];
                if (i == 0) {
                    searchResult.addAlias(alias, alias, JoinType.INNER);
                } else {
                    searchResult.addAlias(split[i - 1] + "." + alias, alias, JoinType.INNER);
                }
            }
        }
        return searchResult;
    }

    /**
     * Gets the search property path, handling nested properties correctly
     *
     * @param field the field
     * @return the property path to use in predicates
     */
    public static String getSearchProperty(Field field) {
        if (field.getProperty().contains(".")) {
            final String[] split = field.getProperty().split("[.]");
            return split[split.length - 2] + "." + split[split.length - 1];
        }
        return field.getProperty();
    }
}
