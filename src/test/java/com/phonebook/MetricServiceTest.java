package com.phonebook;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

public class MetricServiceTest {
    @Mock
    private MeterRegistry meterRegistry;
    @Mock
    private Counter counter;
    @Mock
    private Timer timer;
    private MetricService metricService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(meterRegistry.counter(anyString(), any(String.class), any(String.class))).thenReturn(counter);
        when(meterRegistry.counter(anyString())).thenReturn(counter);
        when(meterRegistry.timer(anyString())).thenReturn(timer);
        metricService = new MetricService(meterRegistry);
    }

    @Test
    void testIncrementAdd() { metricService.incrementAdd(); verify(counter).increment(); }
    @Test
    void testIncrementSearch() { metricService.incrementSearch(); verify(counter).increment(); }
    @Test
    void testIncrementDelete() { metricService.incrementDelete(); verify(counter).increment(); }
    @Test
    void testIncrementEdit() { metricService.incrementEdit(); verify(counter).increment(); }
    @Test
    void testIncrementList() { metricService.incrementList(); verify(counter).increment(); }
    @Test
    void testIncrementFailed() { metricService.incrementFailed(); verify(counter).increment(); }
    @Test
    void testIncrementEmpty() { metricService.incrementEmpty(); verify(counter).increment(); }
} 