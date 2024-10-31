//package kz.bars.wellify.monitoring_service.aop;
//
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//@Aspect
//@Component
//public class TransactionAspect {
//
//    @Before("@annotation(transactional)")
//    public void beginTransaction(Transactional transactional) {
//        // Дополнительные действия перед началом транзакции, если требуется
//        // Например, логирование или инициализация транзакционных ресурсов
//    }
//
//    // Обратите внимание, что Spring самостоятельно завершает транзакции.
//    // Здесь могут быть дополнительные настройки для логирования транзакций.
//}
