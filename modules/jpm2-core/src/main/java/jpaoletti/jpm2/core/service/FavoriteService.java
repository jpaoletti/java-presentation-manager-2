package jpaoletti.jpm2.core.service;

import java.util.List;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.dao.FavoriteDAO;
import jpaoletti.jpm2.core.dao.JPADAOListConfiguration;
import jpaoletti.jpm2.core.model.UserFavorite;
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
     * @param username
     * @return
     */
    @Transactional
    public List<UserFavorite> getFavorites(String username) {
        final JPADAOListConfiguration cfg = favoriteDAO.build();
        cfg.withPredicate((cb, root) -> cb.equal(root.get("username"), username));
        return favoriteDAO.list(cfg);
    }

    @Transactional
    public void removeFavorite(String username, String url) throws PMException {
        final JPADAOListConfiguration cfg = favoriteDAO.build();
        cfg.withPredicate((cb, root) -> cb.equal(root.get("username"), username));
        cfg.withPredicate((cb, root) -> cb.equal(root.get("link"), url));
        final UserFavorite fav = favoriteDAO.find(cfg);
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
