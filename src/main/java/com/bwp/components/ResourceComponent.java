package com.bwp.components;

import com.bwp.Main;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.File;
import java.io.IOException;

@Configuration
public class ResourceComponent implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        for(ExternalFile file : ExternalFile.values()) {
            String basePath = "./" + Main.INTEGRATION.dataFolder().getName() + "/" + file.handlerRecord.folder + "/";
            File directory = new File(basePath);
            if (!directory.exists()) {
                boolean success = directory.mkdirs();
                if(!success) Main.LOGGER.error("Can't create directory {}.", directory.getAbsolutePath());
                else Main.LOGGER.info("Directory {} created.", directory.getAbsolutePath());
            }

            //Can be accessed by thymeleaf by for example: th:src="@{/external/people/<filePath>}"

            System.out.println("Registering resource handler for /external/" + file.handlerRecord.folder + "/** to " + directory.getAbsolutePath());

            registry.addResourceHandler("/external/" + file.handlerRecord.folder + "/**")
                    .addResourceLocations("file:" + directory.getAbsolutePath() + File.separator)
                    .setCachePeriod(0)
                    .resourceChain(true)
                    .addResolver(new PathResourceResolver() {
                        @Override
                        protected Resource getResource(String resourcePath, Resource location) throws IOException {
                            Resource resource = super.getResource(resourcePath, location);
                            if (resource == null || !resource.exists()) {
                                return null;
                            }
                            // Validate the resource path is within allowed directory
                            if (!resource.getFile().getCanonicalPath().startsWith(directory.getCanonicalPath())) {
                                return null;
                            }
                            return resource;
                        }
                    });
        }

    }

    public enum ExternalFile {
        ACTORS(new ResourceHandlerRecord("actors"));

        private final ResourceHandlerRecord handlerRecord;

        ExternalFile(ResourceHandlerRecord handlerRecord) {
            this.handlerRecord = handlerRecord;
        }

        public ResourceHandlerRecord getHandlerRecord() {
            return handlerRecord;
        }
    }

    public record ResourceHandlerRecord(String folder) {

        public File file() {
            return new File("./" + Main.INTEGRATION.dataFolder().getName() + "/" + folder);
        }
    }
}