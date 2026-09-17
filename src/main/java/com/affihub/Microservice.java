package com.affihub;

import com.affihub.endpoints.ProductEndpoint;
import com.affihub.endpoints.CategoryEndpoint;
import com.affihub.lib.ApiExceptionMapper;
import com.affihub.lib.CorsFilter;
import com.affihub.service.implement.ProductServiceImplement;
import com.affihub.service.implement.ProductClickServiceImplement;
import com.affihub.service.implement.CategoryServiceImplement;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.net.URI;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class Microservice extends ResourceConfig {
    private static final String HOST = "0.0.0.0";
    private static final int PORT = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
    public static final String BASE_URI = "http://" + HOST + ":" + PORT + "/api/";

    public Microservice() {
        register(ProductEndpoint.class);
        register(CategoryEndpoint.class);
        register(ApiExceptionMapper.class);
        register(CorsFilter.class);
        register(JacksonFeature.class);
        register(ObjectMapperProvider.class);
    }

    public static void main(String[] args) throws IOException {
        ProductServiceImplement.initialize();
        HttpServer server = startServer();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.shutdownNow();
            ProductServiceImplement.shutdown();
            ProductClickServiceImplement.shutdown();
            CategoryServiceImplement.shutdown();
        }));
        System.out.println("Jersey API started at " + BASE_URI);
        try {
            Thread.currentThread().join();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    public static HttpServer startServer() {
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), new Microservice());
    }

    @Provider
    public static class ObjectMapperProvider implements ContextResolver<ObjectMapper> {
        private final ObjectMapper mapper;

        public ObjectMapperProvider() {
            mapper = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        }

        @Override
        public ObjectMapper getContext(Class<?> type) {
            return mapper;
        }
    }
}
