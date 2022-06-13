package jpaoletti.jpm2.core.service;

import java.util.List;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.dao.DAOListConfiguration;
import jpaoletti.jpm2.core.dao.FavoriteDAO;
import jpaoletti.jpm2.core.model.UserFavorite;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author jpaoletti
 */
@Service
public class FavoriteService {

    @Autowired
    private FavoriteDAO favoriteDAO;

    /**
     * Get favorite list
     *
     * @return
     */
    @Transactional
    public List<UserFavorite> getFavorites(String username) {
        return favoriteDAO.list(new DAOListConfiguration(Restrictions.eq("username", username)));
    }

    @Transactional
    public void removeFavorite(String username, String url) throws PMException {
        final UserFavorite fav = favoriteDAO.find(new DAOListConfiguration(
                Restrictions.eq("username", username),
                Restrictions.eq("link", url)
        ));
        if (fav != null) {
            favoriteDAO.delete(fav);
        }
    }

    @Transactional
    public UserFavorite addFavorite(String username, String title, String url) {
        final UserFavorite fav = new UserFavorite();
        fav.setLink(url);
        fav.setTitle(title);
        fav.setUsername(username);
        favoriteDAO.save(fav);
        return fav;
    }
}
