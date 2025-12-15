package jpaoletti.jpm2.core.validator;

import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.FieldValidator;
import jpaoletti.jpm2.core.security.Group;
import jpaoletti.jpm2.core.security.User;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Validator for the Group.level field that prevents privilege escalation.
 * Ensures users can only set group levels equal to or lower than their own privilege level.
 *
 * Security rule: A user with level N can only create/edit groups with level >= N
 * (equal or lower privilege). This prevents users from creating super-admin groups
 * or elevating existing groups beyond their authority.
 *
 * @author jpaoletti
 */
public class GroupLevelFieldValidator implements FieldValidator {

    private String message = "jpm.validator.group.level.field.insufficient";

    @Override
    public Message validate(Object object, Object convertedValue) {
        // Allow null - let NotNull validator handle that
        if (convertedValue == null) {
            return null;
        }

        try {
            // Get current authenticated user
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!(principal instanceof User)) {
                // If not authenticated or not a User, allow (system operation)
                return null;
            }

            User currentUser = (User) principal;
            Integer currentUserLevel = currentUser.getMaxPrivilegeLevel();
            Integer targetLevel = (Integer) convertedValue;

            // User can only set levels >= their own level (equal or lower privilege)
            if (targetLevel < currentUserLevel) {
                return MessageFactory.error(
                    getMessage(),
                    targetLevel.toString(),
                    currentUserLevel.toString()
                );
            }

            // Additional check: if editing existing group, verify user can manage current level
            if (object instanceof Group) {
                Group group = (Group) object;
                // Only check if group has an ID (existing group being edited)
                if (group.getId() != null && group.getLevel() != null) {
                    // User must be able to manage the CURRENT level to edit it
                    if (group.getLevel() < currentUserLevel) {
                        return MessageFactory.error(
                            "jpm.validator.group.level.field.cannot.manage",
                            group.getName(),
                            group.getLevel().toString(),
                            currentUserLevel.toString()
                        );
                    }
                }
            }

            return null;

        } catch (Exception e) {
            // If any error occurs, fail-safe by rejecting the operation
            return MessageFactory.error(getMessage(), "unknown", "unknown");
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
