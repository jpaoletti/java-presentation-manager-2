package jpaoletti.jpm2.core.menu;

import java.util.HashSet;
import java.util.Set;
import jpaoletti.jpm2.core.PMCoreObject;
import jpaoletti.jpm2.core.exception.EntityNotFoundException;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.OperationValidator;
import jpaoletti.jpm2.core.model.ValidationException;
import jpaoletti.jpm2.core.model.persistent.JpmMenu;
import jpaoletti.jpm2.core.model.persistent.JpmMenuType;
import org.apache.commons.lang3.StringUtils;

/**
 * Validates a jpmMenu item on add and edit: the entity and operation exist,
 * URL items have a url, the parent is a group, there are no cycles and the
 * tree has at most {@link #MAX_LEVELS} levels.
 *
 * @author jpaoletti
 */
public class JpmMenuValidator extends PMCoreObject implements OperationValidator {

    public static final int MAX_LEVELS = 3;

    @Override
    public void validate(Object object) throws ValidationException {
        final JpmMenu item = (JpmMenu) object;
        if (item.getType() == null) {
            throw error("jpm.menu.validator.typeRequired");
        }
        switch (item.getType()) {
            case ENTITY:
                validateEntity(item);
                break;
            case URL:
                if (StringUtils.isBlank(item.getUrl())) {
                    throw error("jpm.menu.validator.urlRequired");
                }
                if (JpmMenuTree.hasUnsupportedScheme(item.getUrl().trim())) {
                    throw error("jpm.menu.validator.urlScheme");
                }
                break;
            case HEADER:
            case SEPARATOR:
                if (item.getParent() == null) {
                    throw error("jpm.menu.validator.parentRequired");
                }
                break;
            default:
        }
        if (item.getType() != JpmMenuType.GROUP && item.getId() != null && !item.getChildren().isEmpty()) {
            throw error("jpm.menu.validator.hasChildren");
        }
        if (StringUtils.isNotBlank(item.getUserCondition())
                && !JpmMenuTree.USER_CONDITION.matcher(item.getUserCondition().trim()).matches()) {
            throw error("jpm.menu.validator.userCondition");
        }
        validateParent(item);
    }

    private void validateEntity(JpmMenu item) throws ValidationException {
        if (StringUtils.isBlank(item.getEntity())) {
            throw error("jpm.menu.validator.entityRequired");
        }
        final String entityId = item.getEntity().trim();
        try {
            final ContextualEntity ce = getJpm().getContextualEntity(entityId);
            final String op = StringUtils.defaultIfBlank(StringUtils.trimToNull(item.getOperation()), JpmMenuItem.DEFAULT_OPERATION);
            if (ce.getEntity().getOperationWithoutAuth(op, null) == null) {
                throw error("jpm.menu.validator.unknownOperation", entityId, op);
            }
        } catch (EntityNotFoundException e) {
            throw error("jpm.menu.validator.unknownEntity", entityId);
        }
    }

    private void validateParent(JpmMenu item) throws ValidationException {
        final JpmMenu parent = item.getParent();
        if (parent == null) {
            if (levels(item) > MAX_LEVELS) {
                throw error("jpm.menu.validator.maxLevels", String.valueOf(MAX_LEVELS));
            }
            return;
        }
        if (parent.getType() != JpmMenuType.GROUP) {
            throw error("jpm.menu.validator.parentNotGroup");
        }
        int ancestors = 0;
        final Set<Long> seen = new HashSet<>();
        for (JpmMenu p = parent; p != null; p = p.getParent()) {
            if (p.equals(item) || (p.getId() != null && !seen.add(p.getId()))) {
                throw error("jpm.menu.validator.cycle");
            }
            ancestors++;
        }
        if (ancestors + levels(item) > MAX_LEVELS) {
            throw error("jpm.menu.validator.maxLevels", String.valueOf(MAX_LEVELS));
        }
    }

    /**
     * @return levels of the item subtree, the item included.
     */
    private int levels(JpmMenu item) {
        return levels(item, 0);
    }

    private int levels(JpmMenu item, int depth) {
        if (item.getId() == null || depth > MAX_LEVELS) {
            return 1;
        }
        int max = 0;
        for (JpmMenu child : item.getChildren()) {
            max = Math.max(max, levels(child, depth + 1));
        }
        return 1 + max;
    }

    private static ValidationException error(String key, String... args) {
        return new ValidationException(MessageFactory.error(key, args));
    }
}
