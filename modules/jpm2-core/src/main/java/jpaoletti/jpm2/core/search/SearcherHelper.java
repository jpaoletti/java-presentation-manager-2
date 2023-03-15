package jpaoletti.jpm2.core.search;

import jpaoletti.jpm2.core.model.Field;

/**
 *
 * @author jpaoletti
 */
public class SearcherHelper {

    public static Searcher.DescribedCriterion addAliases(Searcher.DescribedCriterion describedCriterion, Field field) {
        if (field.getProperty().contains(".")) {
            final String[] split = field.getProperty().split("[.]");
            for (int i = 0; i < split.length - 1; i++) {
                final String alias = split[i];
                if (i == 0) {
                    describedCriterion.addAlias(alias, alias);
                } else {
                    describedCriterion.addAlias(split[i - 1] + "." + alias, alias);
                }
            }
        }
        return describedCriterion;
    }

    public static String getSearchProperty(Field field) {
        if (field.getProperty().contains(".")) {
            final String[] split = field.getProperty().split("[.]");
            return split[split.length - 2] + "." + split[split.length - 1];
        }
        return field.getProperty();
    }
}
