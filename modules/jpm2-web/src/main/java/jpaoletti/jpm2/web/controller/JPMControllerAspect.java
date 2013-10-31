package jpaoletti.jpm2.web.controller;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 *
 * @author jpaoletti
 */
@Aspect
@Component
public class JPMControllerAspect {

    @Pointcut("execution( * *Controller.list(..))") //@org.springframework.web.bind.annotation.RequestMapping
    public void controllerCall() {
    }

    @Around("controllerCall()")
    public void manageContext(ProceedingJoinPoint joinpoint) throws Throwable {
        System.out.println("HOLA!");
        if (joinpoint != null) {
            joinpoint.proceed();
        }
        System.out.println("CHAU!");
    }
}
