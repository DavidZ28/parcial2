package com.co.david.davila.sprint_cloud_confi_server;

import jdk.jfr.Enabled;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer

public class SprintCloudConfiServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SprintCloudConfiServerApplication.class, args);
	}

}
