package ru.sweetbun.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LogExecutionTimeAspect {

    @Around("@within(ru.sweetbun.log.LogExecutionTime) || @annotation(ru.sweetbun.log.LogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - start;

        log.info("Executed method: {}.{}() in {} ms", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), executionTime);
        return proceed;
    }
}
