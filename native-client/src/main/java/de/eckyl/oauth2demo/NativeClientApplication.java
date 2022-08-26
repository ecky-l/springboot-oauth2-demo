package de.eckyl.oauth2demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class NativeClientApplication implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(NativeClientApplication.class);

    private final WebClient webClient;

    public NativeClientApplication(WebClient webClient) {
        this.webClient = webClient;
    }


    public static void main(String[] args) {
        SpringApplication.run(NativeClientApplication.class, args);
    }

    @Override
    public void run(String... args) {
        final var data = "Hello native world";
        final var endPoint = "/resources/echo";
        LOG.info("Lets send some data ({}) to the {} endpoint of the resource server...", data, endPoint);

        final var request = EchoRequest.builder().data("Hello native world").build();
        final var result = webClient.post()
                .uri("/resources/echo")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        LOG.info("Result of REST call to resource server: {}", result);
    }
}
