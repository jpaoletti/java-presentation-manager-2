package jpaoletti.jpm2.core.service;

import java.util.ArrayList;
import java.util.List;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.dao.DAOListConfiguration;
import jpaoletti.jpm2.core.dao.UserVisibleColumnDAO;
import jpaoletti.jpm2.core.exception.EntityNotFoundException;
import jpaoletti.jpm2.core.exception.NotAuthorizedException;
import jpaoletti.jpm2.core.exception.OperationNotFoundException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.EntityContext;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.UserVisibleColumn;
import static org.apache.commons.lang3.StringUtils.join;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author jpaoletti
 */
@Service
public class MiscEntityService extends JPMServiceBase {

    @Autowired
    private UserVisibleColumnDAO userVisibleColumnDAO;

    /**
     *
     * @param username
     * @param entity
     * @return
     * @throws jpaoletti.jpm2.core.exception.EntityNotFoundException
     * @throws jpaoletti.jpm2.core.exception.OperationNotFoundException
     * @throws jpaoletti.jpm2.core.exception.NotAuthorizedException
     */
    @Transactional
    public List<String> getVisibleColumns(String username, ContextualEntity entity) throws EntityNotFoundException, OperationNotFoundException, NotAuthorizedException, PMException {
        final UserVisibleColumn find = userVisibleColumnDAO.find(new DAOListConfiguration(
                Restrictions.eq("entity", entity.toString()),
                Restrictions.eq("username", username))
        );
        if (find == null) {
            final List<String> res = new ArrayList<>();
            for (Field field : entity.getEntity().getOrderedFields(entity.getContext())) {
                final Converter converter = field.getConverter(null, entity.getEntity().getOperation("list", null));
                if (converter != null) {
                    if (field.shouldDisplay("list")) {
                        res.add(field.getId());
                    }
                }
            }
            return res;
        } else {
            return List.of(find.getColumns().split("[,]"));
        }
    }

    @Transactional
    public void setVisibleColumns(String username, ContextualEntity entity, List<String> columns) throws PMException {
        UserVisibleColumn find = userVisibleColumnDAO.find(new DAOListConfiguration(
                Restrictions.eq("entity", entity.toString()),
                Restrictions.eq("username", username))
        );
        if (find == null) {
            find = new UserVisibleColumn();
            find.setEntity(entity.toString());
            find.setUsername(username);
            find.setColumns(join(columns, ","));
            userVisibleColumnDAO.save(find);
        } else {
            find.setColumns(join(columns, ","));
            userVisibleColumnDAO.update(find);
        }
    }

}
