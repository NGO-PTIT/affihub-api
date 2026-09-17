package com.affihub.lib.db;

import com.affihub.config.PostgresConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Queue;

public class PostgresConnectionPool implements AutoCloseable {
    private final PostgresConfig config;
    private final int maxActive;
    private final int maxIdle;
    private final long borrowTimeoutMillis;
    private final Queue<Connection> idleConnections = new ArrayDeque<>();

    private int totalConnections;
    private boolean closed;

    public PostgresConnectionPool(PostgresConfig config, int initialIdle, int maxActive, int maxIdle,
                                  long borrowTimeoutMillis
    ) {
        this.config = config;
        this.maxActive = maxActive;
        this.maxIdle = maxIdle;
        this.borrowTimeoutMillis = borrowTimeoutMillis;
        init(initialIdle);
    }

    public synchronized Connection borrowConnection() {
        if (closed) {
            throw new DbException("Postgres connection pool is closed");
        }

        long deadline = System.currentTimeMillis() + borrowTimeoutMillis;

        while (true) {
            Connection idleConnection = pollValidIdleConnection();
            if (idleConnection != null) {
                return idleConnection;
            }

            if (totalConnections < maxActive) {
                totalConnections++;
                try {
                    return openConnection();
                } catch (SQLException exception) {
                    totalConnections--;
                    throw new DbException("Cannot open PostgreSQL connection", exception);
                }
            }

            long waitMillis = deadline - System.currentTimeMillis();
            if (waitMillis <= 0) {
                throw new DbException("Timeout waiting for PostgreSQL connection");
            }

            try {
                wait(waitMillis);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new DbException("Interrupted while waiting for PostgreSQL connection", exception);
            }
        }
    }

    public synchronized void returnConnection(Connection connection) {
        if (connection == null) {
            return;
        }

        if (closed || !isValid(connection) || idleConnections.size() >= maxIdle) {
            closeConnection(connection);
            totalConnections--;
        } else {
            idleConnections.offer(connection);
        }

        notifyAll();
    }

    @Override
    public synchronized void close() {
        closed = true;
        while (!idleConnections.isEmpty()) {
            closeConnection(idleConnections.poll());
            totalConnections--;
        }
        notifyAll();
    }

    private void init(int initialIdle) {
        for (int index = 0; index < initialIdle; index++) {
            try {
                idleConnections.offer(openConnection());
                totalConnections++;
            } catch (SQLException exception) {
                close();
                throw new DbException("Cannot initialize PostgreSQL connection pool", exception);
            }
        }
    }

    private Connection pollValidIdleConnection() {
        while (!idleConnections.isEmpty()) {
            Connection connection = idleConnections.poll();
            if (isValid(connection)) {
                return connection;
            }
            closeConnection(connection);
            totalConnections--;
        }
        return null;
    }

    private Connection openConnection() throws SQLException {
        return DriverManager.getConnection(
                config.getJdbcUrl(),
                config.getUserName(),
                config.getPassWord()
        );
    }

    private boolean isValid(Connection connection) {
        try {
            return !connection.isClosed() && connection.isValid(2);
        } catch (SQLException exception) {
            return false;
        }
    }

    private void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException ignored) {
        }
    }
}
