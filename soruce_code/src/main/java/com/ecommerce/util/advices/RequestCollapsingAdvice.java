package com.ecommerce.util.advices;

import com.ecommerce.util.requestCollapsing.RequestCollapsingService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(1)
@AllArgsConstructor
public class RequestCollapsingAdvice {

    private final RequestCollapsingService requestCollapsingService;
    private final ParameterNameDiscoverer parameterNameDiscoverer;
    private final ExpressionParser parser = new SpelExpressionParser();

    @SneakyThrows // lombok byte code magic to throw the original exception  back
    @Around("@annotation(reqCollapsing)")
    public Object requestCollapsingAdvice(final ProceedingJoinPoint joinPoint, final ReqCollapsing reqCollapsing){

        final StringBuilder key = new StringBuilder(joinPoint.getSignature().toShortString());
        key.append("::");

        // context for parser to get values from
        EvaluationContext evaluationContext = new MethodBasedEvaluationContext(
                joinPoint.getTarget(),
                ((MethodSignature)(joinPoint.getSignature())).getMethod(),
                joinPoint.getArgs(),
                parameterNameDiscoverer
        );
        for(String k : reqCollapsing.keys()){

            key.append(':').append(parser.parseExpression(k).getValue(evaluationContext));
        }

        return requestCollapsingService.execute(key.toString(), () -> this.proceed(joinPoint));
    }
    @SneakyThrows
    private Object proceed(final ProceedingJoinPoint joinPoint)
    {
        return joinPoint.proceed();
    }
}
