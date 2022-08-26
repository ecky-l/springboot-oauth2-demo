package de.eckyl.oauth2demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.net.InetSocketAddress;

@Configuration
public class WebClientConfig {

    @Bean
    WebClient webClient(OAuth2AuthorizedClientManager authorizedClientManager,
                        @Value("${app.resource-server-url}") String resourceServerUrl,
                        ReactorClientHttpConnector clientHttpConnector) {
        final var oauth2Client = new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);

        return WebClient.builder()
                .clientConnector(clientHttpConnector)
                .baseUrl(resourceServerUrl)
                .apply(oauth2Client.oauth2Configuration())
                .build();
    }

    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository) {

        OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
                .authorizationCode()
                .refreshToken()
                .build();

        DefaultOAuth2AuthorizedClientManager authorizedClientManager =
                new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientRepository);

        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }


    @Bean
    @Scope("prototype")
    ReactorClientHttpConnector reactorClientHttpConnector(
            @Value("${app.proxy-host}") String proxyHost,
            @Value("${app.proxy-port}") Integer proxyPort) {
        final var httpClient = HttpClient
                .create()
                .proxy(proxy -> proxy
                        .type(ProxyProvider.Proxy.HTTP)
                        .address(new InetSocketAddress(proxyHost, proxyPort)));
        return new ReactorClientHttpConnector(httpClient);
    }

}
