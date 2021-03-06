package com.search.server;

import java.util.Collections;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.zookeeper.config.LeaderInitiatorFactoryBean;

@EnableIntegration
@SpringBootApplication
public class Application {
	
   @Autowired
   Environment env;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public LeaderInitiatorFactoryBean leaderInitiator(CuratorFramework client) throws Exception {
        return new LeaderInitiatorFactoryBean().setClient(client).setPath("/stuff").setRole("cluster");
    }

    @Bean
    public LeaderHandler leaderHandler() {
        return new LeaderHandler();
    }
    

    

    

}
