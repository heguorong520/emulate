package com.emulate.database.sharding;

import lombok.Getter;
import lombok.Setter;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.connection.MasterSlaveConnection;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * 定义自己的数据库连接,用于替换原来的连接
 * 做sharding降级
 */
@Getter
@Setter
public class MyMasterSlaveConnection implements Connection {

    private final MasterSlaveConnection masterSlaveConnection;

    private final Map<String, DataSource> dataSourceMap;

    private Connection connection;

    public MyMasterSlaveConnection(MasterSlaveConnection masterSlaveConnection, Map<String, DataSource> dataSourceMap) {
        this.masterSlaveConnection = masterSlaveConnection;
        this.dataSourceMap = dataSourceMap;
        createNativeConnection();
    }

    /**
     * 获取原生的数据库链接
     */
    public void createNativeConnection(){
        try {
            //默认使用主库原生连接进行数据库异常降级
            connection = dataSourceMap.get("master").getConnection();
        }catch (SQLException throwables) {
            throwables.printStackTrace();
            //获取数据库连失败
        }
    }

    @Override
    public Statement createStatement() throws SQLException {
        return masterSlaveConnection.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return masterSlaveConnection.prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return masterSlaveConnection.prepareCall(sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return masterSlaveConnection.nativeSQL(sql);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
       masterSlaveConnection.setAutoCommit(autoCommit);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return masterSlaveConnection.getAutoCommit();
    }

    @Override
    public void commit() throws SQLException {
        masterSlaveConnection.commit();
    }

    @Override
    public void rollback() throws SQLException {
        masterSlaveConnection.rollback();
    }

    @Override
    public void close() throws SQLException {
        masterSlaveConnection.close();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return masterSlaveConnection.isClosed();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return  masterSlaveConnection.getMetaData();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        masterSlaveConnection.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return masterSlaveConnection.isReadOnly();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
         masterSlaveConnection.setCatalog(catalog);
    }

    @Override
    public String getCatalog() throws SQLException {
        return masterSlaveConnection.getCatalog();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        masterSlaveConnection.setTransactionIsolation(level);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return masterSlaveConnection.getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return masterSlaveConnection.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
         masterSlaveConnection.clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return masterSlaveConnection.createStatement(resultSetType,resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return masterSlaveConnection.prepareStatement(sql,resultSetType,resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return masterSlaveConnection.prepareCall(sql,resultSetType,resultSetConcurrency);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return masterSlaveConnection.getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        masterSlaveConnection.setTypeMap(map);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        masterSlaveConnection.setHoldability(holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
        return masterSlaveConnection.getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return masterSlaveConnection.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return masterSlaveConnection.setSavepoint(name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        masterSlaveConnection.rollback(savepoint);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        masterSlaveConnection.releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return masterSlaveConnection.createStatement(resultSetType,resultSetConcurrency,resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return masterSlaveConnection.prepareStatement(sql,resultSetType,resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return masterSlaveConnection.prepareCall(sql,resultSetType,resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return masterSlaveConnection.prepareStatement(sql,autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return masterSlaveConnection.prepareStatement(sql,columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return masterSlaveConnection.prepareStatement(sql,columnNames);
    }

    @Override
    public Clob createClob() throws SQLException {
        return masterSlaveConnection.createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        return masterSlaveConnection.createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return masterSlaveConnection.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return masterSlaveConnection.createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return masterSlaveConnection.isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        masterSlaveConnection.setClientInfo(name,value);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        masterSlaveConnection.setClientInfo(properties);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return masterSlaveConnection.getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return masterSlaveConnection.getClientInfo();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return masterSlaveConnection.createArrayOf(typeName,elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return masterSlaveConnection.createStruct(typeName,attributes);
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        masterSlaveConnection.setSchema(schema);
    }

    @Override
    public String getSchema() throws SQLException {
        return masterSlaveConnection.getSchema();
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        masterSlaveConnection.abort(executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        masterSlaveConnection.setNetworkTimeout(executor,milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return masterSlaveConnection.getNetworkTimeout();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return masterSlaveConnection.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return masterSlaveConnection.isWrapperFor(iface);
    }
}
