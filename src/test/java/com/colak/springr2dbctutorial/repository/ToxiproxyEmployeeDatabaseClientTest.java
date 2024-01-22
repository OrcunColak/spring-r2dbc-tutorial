package com.colak.springr2dbctutorial.repository;

import com.colak.springr2dbctutorial.jpa.Employee;
import eu.rekawek.toxiproxy.Proxy;
import eu.rekawek.toxiproxy.ToxiproxyClient;
import eu.rekawek.toxiproxy.model.ToxicDirection;
import eu.rekawek.toxiproxy.model.toxic.Latency;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.ToxiproxyContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;

// See https://www.atomicjar.com/2023/03/developing-resilient-applications-with-toxiproxy-and-testcontainers/
@SpringBootTest
@Testcontainers
@Slf4j
class ToxiproxyEmployeeDatabaseClientTest {
    private static final Network network = Network.newNetwork();

    @Container
    //  @ServiceConnection
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withNetwork(network)
            .withNetworkAliases("postgres");
    @Container
    private static final ToxiproxyContainer toxiproxy = new ToxiproxyContainer("ghcr.io/shopify/toxiproxy:2.5.0")
            .withNetwork(network);

    @Autowired
    private EmployeeDatabaseClientRepository employeeDatabaseClientRepository;

    private static Proxy postgresqlProxy;

    @DynamicPropertySource
    static void sqlserverProperties(DynamicPropertyRegistry registry) throws IOException {
        var toxiproxyClient = new ToxiproxyClient(toxiproxy.getHost(), toxiproxy.getControlPort());
        postgresqlProxy = toxiproxyClient.createProxy("postgresql", "0.0.0.0:8666", "postgres:5432");
        var r2dbcUrl = "r2dbc:postgresql://%s:%d/%s".formatted(toxiproxy.getHost(), toxiproxy.getMappedPort(8666), postgres.getDatabaseName());
        registry.add("spring.r2dbc.url", () -> r2dbcUrl);
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);
    }

    @Test
    void withLatency() throws IOException {
        // a latency of 1600 ms +/- 100 ms will be created on the way from server to the client
        postgresqlProxy.toxics().latency("postgresql-latency", ToxicDirection.DOWNSTREAM, 1600)
                .setJitter(100);

        StepVerifier.create(employeeDatabaseClientRepository.findAll())
                .expectNextCount(4)
                .verifyComplete();
        postgresqlProxy.toxics().get("postgresql-latency").remove();
    }

    @Test
    void withLatencyWithTimeout() throws IOException {
        // a latency of 1600 ms +/- 100 ms will be created on the way from server to the client
        postgresqlProxy.toxics().latency("postgresql-latency", ToxicDirection.DOWNSTREAM, 1600)
                .setJitter(100);

        Flux<Employee> flux = employeeDatabaseClientRepository
                .findAll()
                // We don’t want our database operation to hang for too long
                .timeout(Duration.ofMillis(50));

        StepVerifier.create(flux)
                // A timeout of 50 ms has been configured in our reactive code and due to the latency being higher,
                // a TimeoutException is produced and captured by test.
                .expectError(TimeoutException.class)
                .verify();
        postgresqlProxy.toxics().get("postgresql-latency").remove();
    }

    @Test
    void withLatencyWithRetries() throws IOException {
        // a latency of 1600 ms +/- 100 ms will be created on the way from server to the client
        Latency latency = postgresqlProxy.toxics().latency("postgresql-latency", ToxicDirection.DOWNSTREAM, 1600)
                .setJitter(100);

        // 2 maximum attempts with a delay of 1s for only TimeoutException has been configured.
        RetryBackoffSpec retryBackoffSpec = Retry
                .fixedDelay(2, Duration.ofSeconds(1))
                .filter(throwable -> throwable instanceof TimeoutException)
                .doBeforeRetry(retrySignal -> log.info(retrySignal.copy().toString()));

        //
        Flux<Employee> flux = employeeDatabaseClientRepository.findAll()
                // We don’t want our database operation to hang for too long
                .timeout(Duration.ofSeconds(1))
                .retryWhen(retryBackoffSpec);

        StepVerifier.create(flux
                )
                .expectSubscription()
                // No events are expected for about 4 seconds.
                .expectNoEvent(Duration.ofSeconds(4))
                .then(() -> {
                    // Finally, latency is removed and therefore records are retrieved.
                    try {
                        latency.remove();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .expectNextCount(4)
                .expectComplete()
                .verify();
    }

    @Test
    void withConnectionDown() throws IOException {
        // Downstream means the direction from server to the client
        postgresqlProxy.toxics().bandwidth("postgres-cut-connection-downstream", ToxicDirection.DOWNSTREAM, 0);
        postgresqlProxy.toxics().bandwidth("postgres-cut-connection-upstream", ToxicDirection.UPSTREAM, 0);
        Flux<Employee> flux = employeeDatabaseClientRepository
                .findAll()
                .timeout(Duration.ofSeconds(5));
        StepVerifier.create(flux)
                .verifyErrorSatisfies(throwable -> assertThat(throwable).isInstanceOf(TimeoutException.class));

        postgresqlProxy.toxics().get("postgres-cut-connection-downstream").remove();
        postgresqlProxy.toxics().get("postgres-cut-connection-upstream").remove();
        StepVerifier.create(this.employeeDatabaseClientRepository.findAll()).expectNextCount(4).verifyComplete();
    }

}
