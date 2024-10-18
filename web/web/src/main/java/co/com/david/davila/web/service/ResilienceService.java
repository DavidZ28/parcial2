package co.com.david.davila.web.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class ResilienceService {

    private final WebClient webClient;

    public ResilienceService() {
        this.webClient = WebClient.create();
    }

    @Retry(name = "orchestratorRetry", fallbackMethod = "fallbackMethod")
    @CircuitBreaker(name = "orchestratorCircuitBreaker", fallbackMethod = "fallbackMethod")
    public Mono<ResponseEntity<String>> orchestrateWithResilience(String url, String payload) {
        return Mono.defer(() ->
                        webClient.post()
                                .uri(url)
                                .header("Content-Type", "application/json")
                                .bodyValue(payload)
                                .retrieve()
                                .toEntity(String.class)
                ).retryWhen(
                        reactor.util.retry.Retry.fixedDelay(5, Duration.ofSeconds(2))
                                .doBeforeRetry(retrySignal ->
                                        System.out.println("Reintentando conexión a " + url + " - Intento: " + (retrySignal.totalRetries() + 1))
                                )
                )
                .onErrorResume(e -> {
                    System.err.println("Error en la solicitud: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(500).body("Error en la solicitud: Circuito abierto"));
                });
    }

    public Mono<Void> callWebhook(String message) {
        return webClient.post()
                .uri("http://localhost:8083/receive") // Cambia esto a la URL correcta de tu webhook
                .header("Content-Type", "application/json")
                .bodyValue("{ \"message\": \"" + message + "\" }") // Ajusta el payload según tus necesidades
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(e -> System.err.println("Error al llamar al webhook: " + e.getMessage()));
    }

    public Mono<ResponseEntity<String>> fallbackMethod(Throwable t) {
        return Mono.just(ResponseEntity.status(500).body("El circuito se ha abierto o se alcanzó el límite de reintentos: " + t.getMessage()));
    }
}
