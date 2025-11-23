package com.ridemate.app.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoggingAspectTest {

    @InjectMocks
    private LoggingAspect loggingAspect;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private Signature signature;

    @BeforeEach
    void setUp() {
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("testMethod");
        when(joinPoint.getTarget()).thenReturn(new TestService());
    }

    @Test
    void logMethodEntry_ShouldLogEntry() {
        Object[] args = new Object[] { "arg1", "arg2" };
        when(joinPoint.getArgs()).thenReturn(args);

        loggingAspect.logMethodEntry(joinPoint);

        verify(joinPoint, times(1)).getSignature();
        verify(joinPoint, times(1)).getArgs();
    }

    @Test
    void logMethodExit_ShouldLogExit() {
        Object result = "testResult";

        loggingAspect.logMethodExit(joinPoint, result);

        verify(joinPoint, times(1)).getSignature();
    }

    @Test
    void logException_ShouldLogException() {
        Throwable exception = new RuntimeException("Test exception");

        loggingAspect.logException(joinPoint, exception);

        verify(joinPoint, times(1)).getSignature();
    }

    private static class TestService {
    }
}
