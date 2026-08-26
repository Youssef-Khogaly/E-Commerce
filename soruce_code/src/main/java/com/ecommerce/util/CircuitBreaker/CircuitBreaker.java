package com.ecommerce.util.CircuitBreaker;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import static com.ecommerce.util.CircuitBreaker.CircuitBreaker.StateEnum.CLOSED;

public class CircuitBreaker {

    @Getter
    private final int openCircuitCooldown;
    @Getter
    private final int halfOpenThreshHold;
    @Getter
    private final int closedFailureThreshold;
    @Getter
    AtomicReference<State> stateSnapShot;
    @Getter
    private final List<Class<? extends Throwable>> trackedException;

    @Builder
    static record State(StateEnum state, int failureCounter,int successCounter, int requestCounter, long openCircuitTimeStamp){}
    public static enum StateEnum {
        CLOSED,HALF_OPEN,OPEN
    }
    @SafeVarargs
    public CircuitBreaker(int openCircuitCooldown, int halfOpenThreshHold, int closedFailureThreshold, final Class<? extends Throwable>... exceptions) {
        this.openCircuitCooldown = openCircuitCooldown;
        this.halfOpenThreshHold = halfOpenThreshHold;
        this.closedFailureThreshold = closedFailureThreshold;
        this.stateSnapShot = new AtomicReference<>(State.builder().state(CLOSED).failureCounter(0).successCounter(0).requestCounter(0).openCircuitTimeStamp(0).build());

        this.trackedException = new ArrayList<>(exceptions.length);
        trackedException.addAll(Arrays.asList(exceptions));
    }

    private void evaluateState() throws CircuitBreakerOpenException {
        State currentState;
        State newState;
        while (true) {
            currentState = stateSnapShot.get();

            switch (currentState.state) {
                case CLOSED -> {
                    return;
                }
                case OPEN -> {
                    if (Instant.now().toEpochMilli() - currentState.openCircuitTimeStamp >= openCircuitCooldown) {
                        newState = State.builder().state(StateEnum.HALF_OPEN).failureCounter(0).successCounter(0).requestCounter(1).openCircuitTimeStamp(Instant.now().toEpochMilli()).build();
                        if (stateSnapShot.compareAndSet(currentState, newState))
                            return;
                        else
                            continue; // another thread modified state, retry
                    } else // still in cool down or other thread changed state
                    {
                        throw new CircuitBreakerOpenException("Circuit breaker open");
                    }
                }
                case HALF_OPEN -> {
                    if (currentState.requestCounter + 1 > halfOpenThreshHold) {
                        throw new CircuitBreakerOpenException("Circuit breaker open");// threshold exceed
                        // do not change state , let another running request handle  state
                    } else {
                        newState = State.builder().state(StateEnum.HALF_OPEN).failureCounter(currentState.failureCounter)
                                .successCounter(currentState.successCounter)
                                .requestCounter(currentState.requestCounter + 1)
                                .openCircuitTimeStamp(currentState.openCircuitTimeStamp).build();

                        if (stateSnapShot.compareAndSet(currentState, newState))
                            return;
                        else
                            continue; // another thread modified state, retry
                    }
                }

            }
        }
    }

    private void onFailure(){
        State currentState;
        State newState;

        while(true)
        {
            currentState = stateSnapShot.get();
            switch (currentState.state)
            {
                case CLOSED -> {
                    //  a lot of failed consecutive requests and state is closed
                    if (currentState.failureCounter() + 1 >= closedFailureThreshold)
                    {
                        newState = State.builder().state(StateEnum.OPEN).failureCounter(0)
                                .successCounter(0)
                                .requestCounter(0)
                                .openCircuitTimeStamp(Instant.now().toEpochMilli()).build();

                        if(stateSnapShot.compareAndSet(currentState,newState))
                            return;
                        else
                            continue; // another thread modified state, retry
                    }
                    return;
                }
                case HALF_OPEN ->
                {
                    newState = State.builder().state(StateEnum.OPEN).failureCounter(0)
                            .successCounter(0)
                            .requestCounter(0)
                            .openCircuitTimeStamp(Instant.now().toEpochMilli()).build();

                    if(stateSnapShot.compareAndSet(currentState,newState))
                        return;
                    else
                        continue; // another thread modified state, retry

                }
                case OPEN -> {

                    // nothing, another thread opened the circuit and handled state
                }
            }
        }



    }
    private void onSuccess(){
        State currentState;
        State newState;
        while (true)
        {
            currentState = stateSnapShot.get();
            switch (currentState.state)
            {
                case CLOSED -> {
                    newState = State.builder()
                            .state(CLOSED)
                            .failureCounter(0)
                            .successCounter(currentState.successCounter)
                            .requestCounter(currentState.requestCounter)
                            .openCircuitTimeStamp(currentState.openCircuitTimeStamp).build();
                    if(stateSnapShot.compareAndSet(currentState,newState))
                        return;
                    else
                        continue; // retry
                }
                case OPEN -> {
                    return;
                }
                case HALF_OPEN ->
                {
                    if(currentState.successCounter + 1 >= halfOpenThreshHold)
                    {
                        newState = State.builder()
                                .state(CLOSED)
                                .failureCounter(0)
                                .successCounter(0)
                                .requestCounter(0)
                                .openCircuitTimeStamp(currentState.openCircuitTimeStamp).build();
                    }
                    else {
                        newState = State.builder()
                                .state(StateEnum.HALF_OPEN)
                                .failureCounter(currentState.failureCounter)
                                .successCounter(currentState.successCounter+1)
                                .requestCounter(currentState.requestCounter)
                                .openCircuitTimeStamp(currentState.openCircuitTimeStamp).build();
                    }
                    if(stateSnapShot.compareAndSet(currentState,newState))
                        return;
                    else
                        continue; // retry

                }
            }

        }


    }
    private boolean isTrackedException(Throwable throwable)
    {
        for(var clazz : trackedException)
        {
                if(clazz.isAssignableFrom(throwable.getClass()))
                    return true;
        }
        return false;
    }
    public <T> T execute(Callable<T> callable) throws Exception {

        evaluateState();


        try{
            T ret =  callable.call();
            onSuccess();
            return ret;
        } catch (Exception e) {
            // tracked exception , handle failure
            if(isTrackedException(e))
                onFailure();

            throw e;
        }
    }
}
