package jpaoletti.jpm2.core.search;

import jpaoletti.jpm2.core.model.Field;

/**
 *
 * @author jpaoletti
 */
public class SearcherHelper {

    public static Searcher.DescribedCriterion addAliases(Searcher.DescribedCriterion describedCriterion, Field field) {
        if (field.getProperty().contains(".")) {//need an alias
            final String alias = field.getProperty().substring(0, field.getProperty().indexOf("."));
            describedCriterion.addAlias(alias, alias);
        }
        return describedCriterion;
    }

}
