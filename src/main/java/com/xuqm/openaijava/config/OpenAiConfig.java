package com.xuqm.openaijava.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * OpenAi配置类
 * 
 * @author xuqiming
 * @date 2023-04-22
 **/
@Data
@Configuration
@ConfigurationProperties(prefix = "openai")
public class OpenAiConfig {

    /**
     * OpenAi的key
     */
    private String key;

    /**
     * OpenAi的host
     */
    private String host;

}
