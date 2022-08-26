package de.eckyl.oauth2demo;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.client.endpoint.NimbusJwtClientAuthenticationParametersConverter;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest;
import org.springframework.security.oauth2.client.endpoint.ReactiveOAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.UnAuthenticatedServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.net.InetSocketAddress;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.function.Function;

@Configuration
public class NativeClientConfig {

    @Bean
    WebClient webClient(
            ReactiveClientRegistrationRepository clientRegistrations,
            ReactiveOAuth2AccessTokenResponseClient<OAuth2ClientCredentialsGrantRequest> accessTokenResponseClient,
            ReactorClientHttpConnector reactorClientHttpConnector,
            @Value("${app.resource-server-url}") String resourceServerUrl,
            @Value("${oauth2.client-registration-key}") String clientRegistrationId) {

        final var authorizedClientProvider = new UnAuthenticatedServerOAuth2AuthorizedClientRepository();
        final var oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrations,
                authorizedClientProvider);

        oauth.setClientCredentialsTokenResponseClient(accessTokenResponseClient);
        oauth.setDefaultClientRegistrationId(clientRegistrationId);


        return WebClient.builder()
                .clientConnector(reactorClientHttpConnector)
                .baseUrl(resourceServerUrl)
                .filter(oauth)
                .build();
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


    @Bean
    @ConditionalOnProperty(name = "oauth2.client-registration-key", havingValue = "cop-demo-jwt")
    ReactiveOAuth2AccessTokenResponseClient<OAuth2ClientCredentialsGrantRequest> accessTokenResponseClient(
            @Value("${oauth2.cop-demo-jwt.private-key}") String pkcs8PrivateKey,
            ReactorClientHttpConnector reactorClientHttpConnector) {
        final var keyPair = readRSAKeyPair(pkcs8PrivateKey);
        final var rsaPrivateKey = keyPair.getLeft();
        final var rsaPublicKey = keyPair.getRight();

        final Function<ClientRegistration, JWK> jwkResolver = clientRegistration -> {
            if (clientRegistration.getClientAuthenticationMethod().equals(ClientAuthenticationMethod.PRIVATE_KEY_JWT)) {
                // Assuming RSA key type
                return new RSAKey.Builder(rsaPublicKey)
                        .privateKey(rsaPrivateKey)
                        .build();
            }
            return null;
        };

        final var client = new WebClientReactiveClientCredentialsTokenResponseClient();
        client.addParametersConverter(new NimbusJwtClientAuthenticationParametersConverter<>(jwkResolver));
        client.setWebClient(WebClient.builder().clientConnector(reactorClientHttpConnector).build());
        return client;
    }

    private Pair<RSAPrivateKey, RSAPublicKey> readRSAKeyPair(final String pkcs8PrivateKey) {

        try {
            final var keyData = Base64.getDecoder().decode(pkcs8PrivateKey);
            final var keyFactory = KeyFactory.getInstance("RSA");
            final var privateKeySpec = new PKCS8EncodedKeySpec(keyData);
            final var privateKey = (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);
            final var publicKeySpec = new RSAPublicKeySpec(privateKey.getModulus(),
                    ((RSAPrivateCrtKey) privateKey).getPublicExponent());
            final var publicKey = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
            return Pair.of(privateKey, publicKey);
        } catch (Exception e) {
            throw new IllegalStateException("Key reading not possible", e);
        }
    }
}
