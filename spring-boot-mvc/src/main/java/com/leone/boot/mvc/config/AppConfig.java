package com.leone.boot.mvc.config;

import com.leone.boot.mvc.service.ProductService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author leone
 * @since 2018-04-03
 **/
@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean(initMethod = "init", destroyMethod = "destroy")
    public ProductService beanWayService() {
        return new ProductService();
    }

}
