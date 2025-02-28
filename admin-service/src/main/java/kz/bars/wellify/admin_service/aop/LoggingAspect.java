package kz.bars.wellify.admin_service.aop;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Log4j2
public class LoggingAspect {

    @Before("execution(* kz.bars.wellify..service..*(..)) && " +
            "!within(kz.bars.wellify..config..*)")
    public void logBeforeMethod(JoinPoint joinPoint) {
        log.info("Method called: " + joinPoint.getSignature());
        log.info("Arguments: " + Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "execution(* kz.bars.wellify..service..*(..)) && " +
            "!within(kz.bars.wellify..config..*)", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("Method returned: " + joinPoint.getSignature());
        log.info("Return value: " + result);
    }
}
