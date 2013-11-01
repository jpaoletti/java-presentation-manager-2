package jpaoletti.jpm2.web.controller.interfaces;

import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.aspect.ControllerCommit;
import jpaoletti.jpm2.core.aspect.ControllerPrepare;
import jpaoletti.jpm2.core.model.Entity;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author jpaoletti
 */
public interface IAddController {

    public static final String OP_ADD = "add";

    @ControllerCommit
    public ModelAndView addCommit(Entity entity, boolean repeat) throws PMException;

    @ControllerPrepare
    public ModelAndView addPrepare(Entity entity, String lastId) throws PMException;

    @ControllerCommit
    public ModelAndView addWeakCommit(Entity owner, String ownerId, Entity entity, boolean repeat) throws PMException;

    @ControllerPrepare
    public ModelAndView addWeakPrepare(Entity entity, String ownerId, String lastId) throws PMException;

}
