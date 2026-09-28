package jpaoletti.jpm2.core.validator;

import java.util.Collection;
import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.FieldValidator;
import jpaoletti.jpm2.core.security.Group;
import jpaoletti.jpm2.core.security.User;
import jpaoletti.jpm2.core.security.PrivilegeLevelGuard;
import jpaoletti.jpm2.util.JPMUtils;

/**
 * Validator that ensures the current user can only assign groups
 * with privilege level equal or lower than their own.
 *
 * Hierarchy logic: 1 = maximum privilege, higher numbers = lower privilege.
 * A user with level 2 can assign groups with level >= 2 (2, 3, 4, ...)
 * but cannot assign groups with level < 2 (1).
 *
 * @author jpaoletti
 */
public class GroupLevelValidator implements FieldValidator {

    private String message = "jpm.validator.group.level.insufficient";

    @Override
    public Message validate(Object object, Object convertedValue) {
        if (convertedValue == null) {
            return null; // Let NotEmpty validator handle this
        }

        final User currentUser = PrivilegeLevelGuard.getCurrentUser();
        if (currentUser == null) {
            // If not authenticated or not a User, allow (system operation)
            return null;
        }
        try {
            Integer currentUserLevel = currentUser.getMaxPrivilegeLevel();

            // Handle both single Group and Collection of Groups
            if (convertedValue instanceof Collection) {
                Collection<?> groups = (Collection<?>) convertedValue;
                for (Object item : groups) {
                    if (item instanceof Group) {
                        Group group = (Group) item;
                        if (group.getLevel() < currentUserLevel) {
                            return MessageFactory.error(
                                getMessage(),
                                group.getName(),
                                String.valueOf(group.getLevel()),
                                String.valueOf(currentUserLevel)
                            );
                        }
                    }
                }
            } else if (convertedValue instanceof Group) {
                Group group = (Group) convertedValue;
                if (group.getLevel() < currentUserLevel) {
                    return MessageFactory.error(
                        getMessage(),
                        group.getName(),
                        String.valueOf(group.getLevel()),
                        String.valueOf(currentUserLevel)
                    );
                }
            }

            return null; // Validation passed
        } catch (Exception e) {
            // The level of an authenticated user could not be verified: reject
            JPMUtils.getLogger().warn("Error validating group level", e);
            return MessageFactory.error(getMessage(), "?", "?", "?");
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
