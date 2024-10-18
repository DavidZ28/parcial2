package com.co.Serna.Juan.Orquestador.controller;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orchestrate")
public class OrchestationController {

    @Autowired
    private ProducerTemplate producerTemplate;

    @PostMapping
    public ResponseEntity<String> orchestrate(@RequestBody String body) {
        String response = producerTemplate.requestBody("direct:startOrchestration", null, String.class);
        return ResponseEntity.ok(response);
    }
}