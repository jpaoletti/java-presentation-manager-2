package jpaoletti.jpm2.web.controller;

import jpaoletti.jpm2.web.controller.SecurityController.AuthoritiesResultEntity;
import java.util.List;

/**
 * Extension point para que aplicaciones host registren grupos adicionales
 * de permisos en el &aacute;rbol de authorities de JPM2.
 *
 * Cada implementaci&oacute;n representa un nodo ra&iacute;z adicional en el tree
 * de permisos del grupo de usuario.
 */
public interface AdditionalAuthorityProvider {

    /**
     * @return ID &uacute;nico del nodo ra&iacute;z (ej: "api-erp")
     */
    String getRootNodeId();

    /**
     * @return Nombre visible del nodo ra&iacute;z (ej: "API ERP")
     */
    String getRootNodeName();

    /**
     * Retorna las entidades (sub-nodos) con sus operaciones (permisos individuales).
     *
     * @param readonly si true, filtrar solo los permisos que tiene el grupo
     * @param selectedAuthorities lista de authorities del grupo (para filtrar en modo readonly)
     * @return lista de entidades con sus operaciones
     */
    List<AuthoritiesResultEntity> getAuthorities(boolean readonly, List<String> selectedAuthorities);
}
