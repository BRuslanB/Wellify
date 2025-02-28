package kz.bars.wellify.admin_service.aop;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Log4j2
public class TransactionAspect {

    @Before("execution(* kz.bars.wellify..*(..)) && " +
            "(@annotation(org.springframework.transaction.annotation.Transactional))")
    public void beginTransaction(JoinPoint joinPoint) {
        log.info("Starting transaction for method: {}", joinPoint.getSignature().toShortString());
    }

    @AfterReturning("execution(* kz.bars.wellify..*(..)) && " +
            "(@annotation(org.springframework.transaction.annotation.Transactional))")
    public void commitTransaction(JoinPoint joinPoint) {
        log.info("Committing transaction for method: {}", joinPoint.getSignature().toShortString());
    }

    @AfterThrowing(value = "execution(* kz.bars.wellify..*(..)) && " +
            "(@annotation(org.springframework.transaction.annotation.Transactional))", throwing = "ex")
    public void rollbackTransaction(JoinPoint joinPoint, Exception ex) {
        log.info("Rolling back transaction for method: {} due to error: {}", joinPoint.getSignature().toShortString(), ex.getMessage());
    }
}
