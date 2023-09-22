package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@ActiveProfiles("test")
@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    classes = DemoApplication.class
)
@AutoConfigureWebTestClient
@AutoConfigureObservability(metrics = false)
class TraceControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @Test
  void shouldDoTracing() {
    var traceId = "650c1f15efdf18e3a7cd38d79a1bb654";
    var spanId = "00f067aa0ba902b7";
    webTestClient
        .get()
        .uri("/test/trace")
        .header("x-b3-traceid", traceId)
        .header("x-b3-spanid", spanId)
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class)
        .value(response ->
            assertThat(response).isEqualTo(traceId)
        );
  }

  @Test
  void shouldDoTracingWithDefer() {
    var traceId = "650c1f15efdf18e3a7cd38d79a1bb654";
    var spanId = "00f067aa0ba902b7";
    webTestClient
        .get()
        .uri("/test/trace-defer")
        .header("x-b3-traceid", traceId)
        .header("x-b3-spanid", spanId)
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class)
        .value(response ->
            assertThat(response).isEqualTo(traceId)
        );
  }
}