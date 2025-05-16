package com.spring.integration.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Around("execution(* com.spring.integration.service.ExchangeRateFetchService.fetchExchangeRates(..))")
    public Object logAroundFetchExchangeRates(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object result = joinPoint.proceed();

        // Log response
        String responseStr = result != null ? objectMapper.writeValueAsString(result) : "null";
        logger.info("Exiting Method: {}, Result: {}", methodName, responseStr);

        return result;
    }
    @Around("execution(* com.spring.integration.controller.*.*(..))")
    public Object logRequestResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        String requestStr = args != null ? objectMapper.writeValueAsString(args) : "";
        logger.info("Request Method: {}, Arguments: {}", methodName, requestStr);

        Object result = joinPoint.proceed();
        String responseStr = result != null ? objectMapper.writeValueAsString(result) : "";
        logger.info("Response from: {},  => {}", methodName, responseStr);
        return result;
    }

}

