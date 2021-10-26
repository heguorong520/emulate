package com.emulate.database.sharding;

import lombok.Getter;
import org.apache.shardingsphere.shardingjdbc.jdbc.adapter.AbstractDataSourceAdapter;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.context.RuntimeContext;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.MasterSlaveDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;

/**
 * 自定义masterSlave数据库源
 */
@Getter
public class MyMasterSlaveDataSource extends AbstractDataSourceAdapter {

    private final MasterSlaveDataSource masterSlaveDataSource;
    private final Map<String, DataSource> dataSourceMap;

    private final RuntimeContext runtimeContext;

    public MyMasterSlaveDataSource(MasterSlaveDataSource masterSlaveDataSource) throws SQLException {
        super(masterSlaveDataSource.getDataSourceMap());
        this.dataSourceMap = masterSlaveDataSource.getDataSourceMap();
        this.runtimeContext = masterSlaveDataSource.getRuntimeContext();
        this.masterSlaveDataSource = masterSlaveDataSource;
    }

    @Override
    public final MyMasterSlaveConnection getConnection() {
        //这里替换为自己的数据库连接
        return new MyMasterSlaveConnection(masterSlaveDataSource.getConnection(),getDataSourceMap());
    }

    @Override
    protected RuntimeContext getRuntimeContext() {
        return runtimeContext;
    }
}
