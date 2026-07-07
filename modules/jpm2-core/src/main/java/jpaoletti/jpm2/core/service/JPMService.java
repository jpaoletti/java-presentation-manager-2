package jpaoletti.jpm2.core.service;

import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.IdentifiedObject;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.model.PaginatedList;
import jpaoletti.jpm2.core.model.SessionEntityData;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author jpaoletti
 */
public interface JPMService {

    public PaginatedList getWeakList(ContextualEntity centity, String instanceId, ContextualEntity weak, boolean paginated, Integer page, Integer pageSize) throws PMException;

    @Transactional
    public PaginatedList getPaginatedList(ContextualEntity entity, Operation operation, SessionEntityData sessionEntityData, Integer page, Integer pageSize, ContextualEntity owner, String ownerId) throws PMException;

    @Transactional(rollbackFor = Exception.class)
    public IdentifiedObject update(Entity entity, String context, Operation operation, EntityInstance entityInstance, Map<String, String[]> parameters) throws PMException;

    /**
     * Deletes an object of this entity identified by instanceId
     *
     * @param entity the entity: /someEntity/
     * @param context entity context: /someEntity!somecontext/
     * @param operation the operation, usually "show" :
     * /someEntity!somecontext/.../theoperation
     * @param instanceId the instance id:
     * /someEntity!somecontext/instanceId/theoperation
     * @return The deleted object
     * @throws PMException
     */
    @Transactional(rollbackFor = Exception.class)
    public IdentifiedObject delete(Entity entity, String context, Operation operation, String instanceId) throws PMException;

    /**
     * Retrieve an object of this entity, caring about the operation being
     * executed.
     *
     * @param entity the entity: /someEntity/
     * @param context entity context: /someEntity!somecontext/
     * @param operation the operation, usually "show" :
     * /someEntity!somecontext/.../theoperation
     * @param instanceId the instance id:
     * /someEntity!somecontext/instanceId/theoperation
     * @return The object and its identification
     * @throws PMException
     */
    public IdentifiedObject get(Entity entity, String context, Operation operation, String instanceId) throws PMException;

    /**
     * Retrieve an object of this entity, without caring about the operation
     * being executed.
     *
     * @param entity the entity: /someEntity/
     * @param context entity context: /someEntity!somecontext/
     * @param instanceId the instance id: /someEntity!somecontext/instanceId
     * @return The object and its identification
     * @throws PMException
     */
    public IdentifiedObject get(Entity entity, String context, String instanceId) throws PMException;

    @Transactional(rollbackFor = Exception.class)
    public IdentifiedObject save(Entity entity, String context, Operation operation, EntityInstance entityInstance, Map<String, String[]> parameters) throws PMException;

    @Transactional
    public IdentifiedObject save(Entity owner, String ownerId, Entity entity, String context, Operation operation, EntityInstance entityInstance, Map<String, String[]> parameters) throws PMException;

    public Workbook toExcel(Entity entity, SessionEntityData sed, ContextualEntity owner, String ownerId) throws PMException;

    /**
     * Export the current (filtered/sorted) list of the entity to a PDF built
     * dynamically from the entity field configuration and rendered with Apache
     * FOP.
     *
     * @param entity the entity being exported
     * @param sed the session data holding the active filters and sort
     * @param owner the owner contextual entity for weak entities (may be null)
     * @param ownerId the owner instance id for weak entities (may be null)
     * @param fieldOrder the ordered field ids to render as columns; when null
     * or empty the default columns (fields with an explicit "toPdf" config) are
     * used
     * @param landscape true for landscape orientation, false for portrait
     * @param pageSize the page size name (A4, A3, Letter...); defaults to A4
     * @param title the document title; when null/empty the entity plural title
     * is used
     * @return the generated PDF as a byte array
     * @throws PMException on any error
     */
    public byte[] toPdf(Entity entity, SessionEntityData sed, ContextualEntity owner, String ownerId,
            List<String> fieldOrder, boolean landscape, String pageSize, String title) throws PMException;
}
