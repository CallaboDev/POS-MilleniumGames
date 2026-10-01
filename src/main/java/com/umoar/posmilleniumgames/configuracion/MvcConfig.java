package com.umoar.posmilleniumgames.configuracion;

import org.springframework.context.annotation.Configuration;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Vincula la URL "/uploads/**" con la carpeta física "uploads" en el disco local
        String uploadDir = Paths.get("uploads").toFile().getAbsolutePath();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }
package com.umoar.posmilleniumgames.configuracion;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Vincula la URL "/uploads/**" con la carpeta física en disco para mas compatibilidad 
        Path uploadDirPath = Paths.get("uploads");
        String uploadUri = uploadDirPath.toFile().toURI().toString();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadUri);
    }
}
