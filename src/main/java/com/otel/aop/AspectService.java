package com.otel.aop;

import com.otel.metric.RequestCountMetric;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AspectService {

    private final RequestCountMetric requestCountMetric;

    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void getMappingPointcut() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void postMappingPointcut() {
    }

    @Pointcut("getMappingPointcut() || postMappingPointcut()")
    public void requestMappingPointcut() {
    }

    @Around("requestMappingPointcut()")
    public Object aroundMapping(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("Before method: " + joinPoint.getSignature().getName());
        requestCountMetric.incrementRequestCounter();
        Object result = joinPoint.proceed();

        System.out.println("After method: " + joinPoint.getSignature().getName());

        return result;
    }


}
