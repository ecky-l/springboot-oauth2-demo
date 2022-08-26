package de.eckyl.oauth2demo;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

@Controller
public class MvcController {
    private final WebClient webClient;

    public MvcController(WebClient webClient) {
        this.webClient = webClient;
    }

    @RequestMapping(value ="/echo", method = RequestMethod.GET)
    public String echo(@RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient authorizedClient) {
        final var request = EchoRequest.builder().data("Hello World").build();
        final var result = webClient.post()
                .uri("/resources/echo")
                .bodyValue(request)
                .attributes(oauth2AuthorizedClient(authorizedClient))
                .retrieve()
                .bodyToMono(EchoResponse.class)
                .block()
                ;
        System.out.println(result);
        return "redirect:/hello";
    }
}
