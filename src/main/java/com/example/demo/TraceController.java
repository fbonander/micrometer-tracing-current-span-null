package com.example.demo;

import io.micrometer.context.ContextSnapshot;
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequestMapping("/test")
@RestController
@Slf4j
public class TraceController {

  private final Tracer tracer;

  public TraceController(Tracer tracer) {
    this.tracer = tracer;
  }

  @GetMapping("/trace")
  public String trace(@RequestHeader Map<String, String> headers) {
    log.info("TraceId from header: {}", headers.get("x-b3-traceid"));
    var current = this.tracer.currentSpan();
    if (current != null) {
      log.info("currentSpan TraceId: {}, Span Id: {}", current.context().traceId(), current.context().spanId());
    } else {
      log.info("currentSpan is null");
    }
    var span = tracer.nextSpan();
    log.info("nextSpan TraceId: {}, Span Id: {}", span.context().traceId(), span.context().spanId());
    return span.context().traceId();
  }

  @GetMapping("/trace-defer")
  public Mono<String> traceDefer(@RequestHeader Map<String, String> headers) {
    log.info("TraceId from header: {}", headers.get("x-b3-traceid"));
    var current = this.tracer.currentSpan();
    if (current != null) {
      log.info("currentSpan TraceId: {}, Span Id: {}", current.context().traceId(), current.context().spanId());
    } else {
      log.info("currentSpan is null");
    }

    var span = tracer.nextSpan();
    log.info("nextSpan TraceId: {}, Span Id: {}", span.context().traceId(), span.context().spanId());

    return Mono.deferContextual(contextView -> {
      try (ContextSnapshot.Scope scope = ContextSnapshot.setThreadLocalsFrom(contextView,
          ObservationThreadLocalAccessor.KEY)) {
        var context = this.tracer.currentSpan().context();
        log.info("deferContextual currentSpan TraceId: {}, Span Id: {}", context.traceId(), context.spanId());
        return Mono.just(context.traceId());
      }
    });
  }
}
