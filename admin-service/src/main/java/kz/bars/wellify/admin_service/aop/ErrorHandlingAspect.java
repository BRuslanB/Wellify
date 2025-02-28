package kz.bars.wellify.admin_service.aop;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Log4j2
public class ErrorHandlingAspect {

    @AfterThrowing(pointcut = "execution(* kz.bars.wellify..*(..))", throwing = "ex")
    public void logException(Exception ex) {
        log.error("Exception caught: " + ex.getMessage(), ex);
    }
}
