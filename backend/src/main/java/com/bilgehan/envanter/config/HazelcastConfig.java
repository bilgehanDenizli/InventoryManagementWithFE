package com.bilgehan.envanter.config;

import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfig {
    @Bean
    public ClientConfig config() {

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setClusterName("Inventory-Management");
        clientConfig.getNetworkConfig().addAddress("localhost");

        return clientConfig;
    }
}
