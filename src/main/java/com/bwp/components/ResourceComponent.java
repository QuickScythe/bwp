package com.bwp.components;

import com.bwp.Main;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.File;
import java.io.IOException;

/**
 * Spring WebMvc configuration that exposes files under the application's data directory
 * as static resources, under the /external/{folder}/ path.
 * <p>
 * Currently registers a handler for actor headshots and other per-talent assets
 * in data/actors/, allowing Thymeleaf or clients to reference them via
 * /external/actors/<file>.
 */
@Configuration
public class ResourceComponent implements WebMvcConfigurer {

    /**
     * Registers resource handlers for each ExternalFile enum entry. Ensures the backing
     * directories exist, then maps /external/{folder}/** to the corresponding filesystem path.
     * <p>
     * Security: the PathResourceResolver verifies the resolved resource stays within the
     * configured directory by comparing canonical paths.
     *
     * @param registry the Spring ResourceHandlerRegistry to update
     */
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        for(ExternalFile file : ExternalFile.values()) {
            File directory = new File(Main.INTEGRATION.dataFolder(), file.handlerRecord.folder + File.separator).getAbsoluteFile();
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
                        protected Resource getResource(@NonNull String resourcePath, @NonNull Resource location) throws IOException {
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

    /**
     * Enumeration of external resource folders exposed via /external/{folder}/.
     */
    public enum ExternalFile {
        /** Talent/actor-related resources stored under data/actors/. */
        ACTORS(new ResourceHandlerRecord("actors"));

        private final ResourceHandlerRecord handlerRecord;

        ExternalFile(ResourceHandlerRecord handlerRecord) {
            this.handlerRecord = handlerRecord;
        }

        /**
         * Metadata describing how to resolve and expose this external folder.
         *
         * @return the record holding folder configuration
         */
        public ResourceHandlerRecord getHandlerRecord() {
            return handlerRecord;
        }
    }

    /**
     * Immutable descriptor for an externally exposed folder under the data directory.
     *
     * @param folder the relative folder name under the data directory (e.g., "actors")
     */
    public record ResourceHandlerRecord(String folder) {

        /**
         * Resolves the absolute File location for this folder under the integration's data directory.
         *
         * @return absolute File path
         */
        public File file() {
            return new File(Main.INTEGRATION.dataFolder(), folder).getAbsoluteFile();
        }
    }
}