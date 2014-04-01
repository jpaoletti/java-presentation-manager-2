package jpaoletti.jpm2.core.model;

/**
 * An interface that filters the data shown by an entity asociated to another
 * field.
 *
 * @author jpaoletti
 *
 */
public interface RelatedListFilter extends ListFilter {

    public void setRelatedValue(String value);
}
