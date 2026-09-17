package com.affihub.config;

import java.io.InputStream;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

public class MicroServiceConfig {
    private static final String CONFIG_FILE = "MicroService.yaml";

    private final PostgresConfig postgres;

    private MicroServiceConfig(PostgresConfig postgres) {
        this.postgres = postgres;
    }

    public static MicroServiceConfig load() {
        InputStream inputStream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(CONFIG_FILE);

        if (inputStream == null) {
            throw new IllegalStateException(CONFIG_FILE + " not found in classpath");
        }

        Map<String, Object> root = new Yaml().load(inputStream);
        Map<String, Object> postgres = getMap(root, "postgres");

        return new MicroServiceConfig(new PostgresConfig(
                getEnv("POSTGRES_HOST", getString(postgres, "host")),
                getEnv("POSTGRES_USER", getString(postgres, "userName")),
                getEnv("POSTGRES_PASSWORD", getString(postgres, "passWord")),
                getEnv("POSTGRES_DATABASE", getString(postgres, "database")),
                getBoolean(getEnv("POSTGRES_READ_ONLY", String.valueOf(getBoolean(postgres, "readOnly"))))
        ));
    }

    public PostgresConfig getPostgres() {
        return postgres;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getMap(Map<String, Object> source, String key) {
        Object value = source.get(key);
        if (!(value instanceof Map)) {
            throw new IllegalStateException("Missing config section: " + key);
        }
        return (Map<String, Object>) value;
    }

    private static String getString(Map<String, Object> source, String key) {
        Object value = source.get(key);
        if (value == null) {
            return "";
        }
        return String.valueOf(value);
    }

    private static boolean getBoolean(Map<String, Object> source, String key) {
        Object value = source.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return Boolean.parseBoolean(String.valueOf(value));
    }

    private static String getEnv(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private static boolean getBoolean(String value) {
        return Boolean.parseBoolean(value);
    }
}
