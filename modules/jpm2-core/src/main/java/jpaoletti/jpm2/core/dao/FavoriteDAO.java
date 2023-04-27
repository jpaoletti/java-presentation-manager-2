package jpaoletti.jpm2.core.dao;

import jpaoletti.jpm2.core.model.UserFavorite;

/**
 *
 * @author jpaoletti
 */
public class FavoriteDAO extends HibernateCriteriaDAO<UserFavorite, String> {

    @Override
    public String getId(Object object) {
        return Long.toString(((UserFavorite) object).getId());
    }
}
