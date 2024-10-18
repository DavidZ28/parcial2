package co.com.david.davila.web.service;

import co.com.david.davila.web.controller.WebfluxController;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class BatchService {

    private final WebfluxController webfluxController;

    @Autowired
    public BatchService(WebfluxController webfluxController) {
        this.webfluxController = webfluxController;
    }

    @Scheduled(fixedRate = 120000)
    public void executeBatchProcess() {
        System.out.println("Ejecutando el proceso en batch...");
        webfluxController.orchestrate().subscribe(response -> {
            System.out.println("Resultado del proceso orquestador: " + response.getBody());
        });
    }
}