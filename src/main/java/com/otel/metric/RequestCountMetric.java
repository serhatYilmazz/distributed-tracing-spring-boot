package com.otel.metric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class RequestCountMetric {

    private Counter requestCounter;

    public RequestCountMetric(MeterRegistry meterRegistry) {
        requestCounter = Counter.builder("yilmaz.count")
                .description("total request count")
                .register(meterRegistry);
    }

    public void incrementRequestCounter() {
        requestCounter.increment();
    }
}
