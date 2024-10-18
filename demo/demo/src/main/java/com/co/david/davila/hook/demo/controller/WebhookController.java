package com.co.david.davila.hook.demo.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class WebhookController {

    public static void main(String[] args) {
        SpringApplication.run(WebhookController.class, args);
    }

    @PostMapping("/receive")
    public ResponseEntity<String> receiveMessage() {
        // Lógica para procesar el webhook
        System.out.println("Recibido el mensaje del orquestador");
        return ResponseEntity.ok("Mensaje recibido correctamente");
    }
}
