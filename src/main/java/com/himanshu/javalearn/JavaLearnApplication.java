package com.himanshu.javalearn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

// Sign-in is guest-by-name or Google OAuth, so Spring's generated in-memory user is not wanted.
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
@ConfigurationPropertiesScan
public class JavaLearnApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaLearnApplication.class, args);
    }
}
