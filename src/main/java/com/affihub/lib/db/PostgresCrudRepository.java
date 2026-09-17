package com.affihub.lib.db;

import com.affihub.config.PostgresConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostgresCrudRepository implements AutoCloseable {
    private static final int INITIAL_IDLE_CONNECTIONS = 1;
    private static final int MAX_ACTIVE_CONNECTIONS = 20;
    private static final int MAX_IDLE_CONNECTIONS = 10;
    private static final long BORROW_TIMEOUT_MILLIS = 30_000;

    private final PostgresConnectionPool connectionPool;

    public PostgresCrudRepository(PostgresConfig config) {
        this.connectionPool = new PostgresConnectionPool(
                config,
                INITIAL_IDLE_CONNECTIONS,
                MAX_ACTIVE_CONNECTIONS,
                MAX_IDLE_CONNECTIONS,
                BORROW_TIMEOUT_MILLIS
        );
    }

    public void batchInsert(String... insertQueries) {
        Connection connection = connectionPool.borrowConnection();
        try (Statement statement = connection.createStatement()) {

            for (String query : insertQueries) {
                statement.addBatch(query);
            }
            statement.executeBatch();
        } catch (SQLException exception) {
            throw new DbException("Batch insert failed", exception);
        } finally {
            connectionPool.returnConnection(connection);
        }
    }

    public void executeQuery(String query, Object... parameters) {
        Connection connection = connectionPool.borrowConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {

            setParameters(statement, parameters);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new DbException("Execute query failed", exception);
        } finally {
            connectionPool.returnConnection(connection);
        }
    }

    public void executeQueryBatch(String query, List<List<Object>> parametersList) {
        Connection connection = connectionPool.borrowConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {

            if (parametersList != null) {
                for (List<Object> parameters : parametersList) {
                    setParameters(statement, parameters == null ? null : parameters.toArray());
                    statement.addBatch();
                }
            }
            statement.executeBatch();
        } catch (SQLException exception) {
            throw new DbException("Execute query batch failed", exception);
        } finally {
            connectionPool.returnConnection(connection);
        }
    }

    public Map<String, Object> findByPrimaryKeys(String query, Object... parameters) {
        List<Map<String, Object>> results = fetch(query, parameters);
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    public List<Map<String, Object>> fetch(String query, Object... parameters) {
        Connection connection = connectionPool.borrowConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {

            setParameters(statement, parameters);
            try (ResultSet resultSet = statement.executeQuery()) {
                return mapResultSet(resultSet);
            }
        } catch (SQLException exception) {
            throw new DbException("Fetch failed", exception);
        } finally {
            connectionPool.returnConnection(connection);
        }
    }

    @Override
    public void close() {
        connectionPool.close();
    }

    private void setParameters(PreparedStatement statement, Object... parameters) throws SQLException {
        if (parameters == null || parameters.length == 0) {
            return;
        }

        int index = 1;
        for (Object parameter : parameters) {
            statement.setObject(index++, parameter);
        }
    }

    private List<Map<String, Object>> mapResultSet(ResultSet resultSet) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (resultSet.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int index = 1; index <= columnCount; index++) {
                String columnName = metaData.getColumnName(index);
                row.put(columnName, resultSet.getObject(index));
            }
            results.add(row);
        }

        return results;
    }
}
