package jpaoletti.jpm2.core.menu;

import jpaoletti.jpm2.core.dao.DefaultJPADAO;

/**
 * DAO of the jpmMenu entity: clears the menu cache on every change.
 *
 * @author jpaoletti
 */
public class JpmMenuDAO extends DefaultJPADAO {

    private JpmMenuService menuService;

    @Override
    public void save(Object object) {
        super.save(object);
        invalidate();
    }

    @Override
    public void update(Object object) {
        super.update(object);
        invalidate();
    }

    @Override
    public void delete(Object object) {
        super.delete(object);
        invalidate();
    }

    private void invalidate() {
        if (menuService != null) {
            menuService.invalidate();
        }
    }

    public JpmMenuService getMenuService() {
        return menuService;
    }

    public void setMenuService(JpmMenuService menuService) {
        this.menuService = menuService;
    }
}
