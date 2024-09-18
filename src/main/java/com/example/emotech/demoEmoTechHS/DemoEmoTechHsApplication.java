package com.example.emotech.demoEmoTechHS;

//import com.example.emotech.demoEmoTechHS.property.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Is annotated with Spring Boot and serves as the main application entry point.
 * It enables cross-origin resource sharing (CORS) for all origins using the @Bean annotation.
 * Spring Application is run in the main method to initialize the application.
 */
@SpringBootApplication
/*@EnableConfigurationProperties({
	FileStorageProperties.class
})*/
public class DemoEmoTechHsApplication {

	/**
	 * Initializes and runs a Spring Boot application using the `SpringApplication.run`
	 * method. It takes two parameters: an instance of the application's class and an
	 * array of command-line arguments, which are passed to the application for execution.
	 * The application is then launched with these settings.
	 *
	 * @param args command-line arguments passed to the Java application, which can be
	 * used for configuration or customization purposes.
	 */
	public static void main(String[] args) {
		SpringApplication.run(DemoEmoTechHsApplication.class, args);
	}

	/**
	 * Enables cross-origin resource sharing (CORS).
	 * It adds a mapping for all URLs (*) to allow origins from any domain (*).
	 * This allows web applications hosted on different domains to access resources from
	 * another domain.
	 *
	 * @returns a WebMvcConfigurer object that enables CORS with wildcard origin.
	 *
	 * It is an instance of WebMvcConfigurer with a custom configuration for CORS.
	 * The addCorsMappings method sets up a mapping to allow requests from all origins
	 * to all URLs.
	 * The allowedOrigins attribute specifies that no origin check will be performed.
	 */
	@Bean
    public WebMvcConfigurer cors(){

        return new WebMvcConfigurer() {
            /**
             * Overrides the default CORS configuration, allowing all origins to make requests
             * to any endpoint. It adds a mapping for all endpoints ("/**") with an allowed origin
             * of "*". This enables cross-origin resource sharing (CORS) for all domains.
             *
             * @param registry configuration object for CORS (Cross-Origin Resource Sharing),
             * allowing for the specification of allowed origins, methods, and headers.
             */
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*");
            }
        };

    }

}
