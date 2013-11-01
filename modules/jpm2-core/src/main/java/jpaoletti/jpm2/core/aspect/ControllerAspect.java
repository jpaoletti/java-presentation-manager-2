package jpaoletti.jpm2.core.aspect;

import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.util.JPMUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author jpaoletti
 */
@Aspect
public class ControllerAspect {

    @Autowired
    private JPMContext context;

    @Around(value = "execution(* *(..)) && @annotation(jpaoletti.jpm2.core.aspect.ControllerPrepare)")
    public void controllerPrepare(ProceedingJoinPoint joinPoint) throws Throwable {
        JPMUtils.getLogger().info("PREPARE!!!");
        JPMUtils.getLogger().info("hijacked : " + joinPoint.getSignature().getName());
        JPMUtils.getLogger().info(getContext());
        joinPoint.proceed();
    }

    @Around(value = "execution(* *(..)) && @annotation(jpaoletti.jpm2.core.aspect.ControllerCommit)")
    public void controllerCommit(ProceedingJoinPoint joinPoint) throws Throwable {
        JPMUtils.getLogger().info("COMMIT!!!");
        JPMUtils.getLogger().info("hijacked : " + joinPoint.getSignature().getName());
        JPMUtils.getLogger().info("******");
        joinPoint.proceed();
    }

    public JPMContext getContext() {
        return context;
    }

    public void setContext(JPMContext context) {
        this.context = context;
    }
}
