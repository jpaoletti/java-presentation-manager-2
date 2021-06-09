package jpaoletti.jpm2.core.model;

import java.util.List;

/**
 *
 * @author jpaoletti
 */
public interface WithTags {

    public List<? extends Tag> getTags();
}
