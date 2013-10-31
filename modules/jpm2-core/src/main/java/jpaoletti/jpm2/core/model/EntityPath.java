package jpaoletti.jpm2.core.model;

import java.util.ArrayList;
import java.util.List;
import jpaoletti.jpm2.core.PresentationManager;
import jpaoletti.jpm2.core.exception.EntityNotFoundException;

/**
 * En entity path is a representation of something like
 * owner1.owner2.owner3.entity
 *
 * @author jpaoletti
 */
public class EntityPath {

    private PresentationManager jpm;
    private List<Entity> owners;
    private Entity entity;

    public EntityPath() {
        this.owners = new ArrayList<>();
    }

    public EntityPath(PresentationManager jpm, String representation) throws EntityNotFoundException {
        this.owners = new ArrayList<>();
        final String[] split = representation.split("[.]");
        if (split.length > 1) {
            for (int i = 0; i < split.length - 1; i++) {
                final String ownerId = split[i];
                owners.add(jpm.getEntity(ownerId));
            }
        }
        this.entity = jpm.getEntity(split[split.length - 1]);
    }

    public final PresentationManager getJpm() {
        return jpm;
    }

    public void setJpm(PresentationManager jpm) {
        this.jpm = jpm;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (Entity owner : getOwners()) {
            sb.append(owner.getId()).append(".");
        }
        sb.append(getEntity().getId());
        return sb.toString();
    }

    public List<Entity> getOwners() {
        return owners;
    }

    public void setOwners(List<Entity> owners) {
        this.owners = owners;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

}
