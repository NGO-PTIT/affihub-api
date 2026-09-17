package com.affihub.config;

public class PostgresConfig {
    private final String host;
    private final String userName;
    private final String passWord;
    private final String database;
    private final boolean readOnly;

    public PostgresConfig(String host, String userName, String passWord, String database, boolean readOnly) {
        this.host = host;
        this.userName = userName;
        this.passWord = passWord;
        this.database = database;
        this.readOnly = readOnly;
    }

    public String getHost() {
        return host;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public String getDatabase() {
        return database;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public String getJdbcUrl() {
        String separator = host.contains("?") ? "&" : "?";
        return "jdbc:postgresql://" + host + "/" + database + separator + "sslmode=require";
    }
}
