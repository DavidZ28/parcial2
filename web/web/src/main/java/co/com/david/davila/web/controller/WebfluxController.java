package co.com.david.davila.web.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import co.com.david.davila.web.service.ResilienceService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;

@SpringBootApplication
@RestController
public class WebfluxController {

    private final ObjectMapper objectMapper;
    private final ResilienceService resilienceService;

    public WebfluxController(ResilienceService resilienceService) {
        this.objectMapper = new ObjectMapper();
        this.resilienceService = resilienceService;
    }

    @PostMapping("/orchestrate")
    public Mono<ResponseEntity<String>> orchestrate() {
        return resilienceService.orchestrateWithResilience("http://localhost:8080/getStep", createPayload("1"))
                .flatMap(response1 -> extractSingleAnswer(response1.getBody())
                        .flatMap(answer1 -> resilienceService.orchestrateWithResilience("http://localhost:8081/getStep", createPayload("2"))
                                .flatMap(response2 -> extractSingleAnswer(response2.getBody())
                                        .flatMap(answer2 -> resilienceService.orchestrateWithResilience("http://localhost:8082/getStep", createPayload("3"))
                                                .flatMap(response3 -> extractSingleAnswer(response3.getBody())
                                                        .flatMap(answer3 -> {
                                                            String finalResponse = "Responses:\n1: " + answer1 +
                                                                    "\n2: " + answer2 +
                                                                    "\n3: " + answer3;

                                                            // webhook
                                                            return resilienceService.callWebhook(finalResponse)
                                                                    .then(Mono.just(ResponseEntity.ok().body(finalResponse))); // Asegúrate de manejar la respuesta del webhook
                                                        }))))));
    }


    private String createPayload(String step) {
        return "{ \"data\": [{ \"header\": { \"id\": \"12345\", \"type\": \"StepsGiraffeRefrigerator\" }, \"step\": \"" + step + "\" }] }";
    }

    private Mono<String> extractSingleAnswer(String json) {
        return Mono.fromCallable(() -> {
            try {
                JsonNode rootNode = objectMapper.readTree(json);
                if (rootNode.isArray() && !rootNode.isEmpty()) {
                    JsonNode firstDataArray = rootNode.get(0).path("data");
                    if (firstDataArray.isArray() && !firstDataArray.isEmpty()) {
                        JsonNode firstDataNode = firstDataArray.get(0);
                        return firstDataNode.path("answer").asText();
                    }
                }
            } catch (IOException e) {
                System.err.println("Error al procesar el JSON: " + e.getMessage());
            }
            return "Sin respuesta";
        });
    }
}
