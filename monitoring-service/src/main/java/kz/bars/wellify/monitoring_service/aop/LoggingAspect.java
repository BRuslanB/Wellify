package kz.bars.wellify.monitoring_service.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* kz.bars.wellify.monitoring_service..*(..))")
    public void logBeforeMethod(JoinPoint joinPoint) {
        logger.info("Method called: " + joinPoint.getSignature());
        logger.info("Arguments: " + Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "execution(* kz.bars.wellify.monitoring_service..*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        logger.info("Method returned: " + joinPoint.getSignature());
        logger.info("Return value: " + result);
    }
}
