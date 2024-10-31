package kz.bars.wellify.monitoring_service.aop;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ErrorHandlingAspect {

    private static final Logger logger = LoggerFactory.getLogger(ErrorHandlingAspect.class);

    @AfterThrowing(pointcut = "execution(* kz.bars.wellify.monitoring_service..*(..))", throwing = "ex")
    public void logException(Exception ex) {
        logger.error("Exception caught: " + ex.getMessage(), ex);
    }
}
