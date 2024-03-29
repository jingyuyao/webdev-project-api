package com.jingyuyao.webdevprojectapi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiConfiguration {

  @Bean
  public WebMvcConfigurer webMvcConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry
            .addMapping("/**")
            .allowCredentials(true)
            .allowedMethods("*")
            .allowedOrigins(
                "http://localhost:4200",
                "https://jingyuyao-webdev-project.herokuapp.com");
      }
    };
  }
}
