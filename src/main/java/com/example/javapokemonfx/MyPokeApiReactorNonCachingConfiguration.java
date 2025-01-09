package com.example.javapokemonfx;

import io.netty.channel.ChannelOption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import skaro.pokeapi.PokeApiReactorNonCachingConfiguration;

@Configuration
@Import(PokeApiReactorNonCachingConfiguration.class)
public class MyPokeApiReactorNonCachingConfiguration {

    @Bean
    public ConnectionProvider connectionProvider() {
        return ConnectionProvider.create("pokeapi-connection-pool", 100); // 100 connections in the pool
    }

    @Bean
    public HttpClient httpClient(ConnectionProvider connectionProvider) {
        return HttpClient.create(connectionProvider)  // Use the custom connection pool
                .responseTimeout(java.time.Duration.ofSeconds(30)) // Set the response timeout
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);// Set the connection timeout
    }
}
