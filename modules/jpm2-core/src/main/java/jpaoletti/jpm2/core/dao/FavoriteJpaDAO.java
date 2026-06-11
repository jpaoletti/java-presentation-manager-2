package jpaoletti.jpm2.core.dao;

import jpaoletti.jpm2.core.model.UserFavorite;

/**
 *
 * @author jpaoletti
 */
public class FavoriteJpaDAO extends JPADAO<UserFavorite, Long> {

    @Override
    public Long getId(Object object) {
        return ((UserFavorite) object).getId();
    }
}
