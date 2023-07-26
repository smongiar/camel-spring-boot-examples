package sample.observation.config;

import org.springframework.boot.actuate.autoconfigure.tracing.MicrometerTracingAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import sample.observation.logging.LoggingSpanHandler;
import sample.observation.logging.MetricsPrinter;

@Configuration
@AutoConfigureAfter(MicrometerTracingAutoConfiguration.class)
public class MicrometerConfiguration {

	@Bean
	AutoCloseable metricsPrinter(MeterRegistry meterRegistry) {
		return new MetricsPrinter(meterRegistry);
	}

	@Bean
	ObservationHandler<Observation.Context> observationLogger() {
		return new LoggingSpanHandler();
	}

}
