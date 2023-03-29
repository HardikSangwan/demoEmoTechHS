package com.example.emotech.demoEmoTechHS;

//import com.example.emotech.demoEmoTechHS.property.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
/*@EnableConfigurationProperties({
	FileStorageProperties.class
})*/
public class DemoEmoTechHsApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoEmoTechHsApplication.class, args);
	}

	@Bean
    public WebMvcConfigurer cors(){

        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*");
            }
        };

    }

}
