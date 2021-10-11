package com.emulate.database.sharding;

import lombok.Getter;
import org.apache.shardingsphere.shardingjdbc.jdbc.adapter.AbstractDataSourceAdapter;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.context.RuntimeContext;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.MasterSlaveDataSource;

import java.sql.SQLException;

/**
 * 自定义masterSlave数据库源
 */
@Getter
public class MyMasterSlaveDataSource extends AbstractDataSourceAdapter {

    private final MasterSlaveDataSource masterSlaveDataSource;

    private final MyMasterSlaveConnection myMasterSlaveConnection;

    public MyMasterSlaveDataSource(MasterSlaveDataSource masterSlaveDataSource) throws SQLException {
        super(masterSlaveDataSource.getDataSourceMap());
        this.masterSlaveDataSource = masterSlaveDataSource;
        //在此使用自定义数据库连接
        myMasterSlaveConnection = new MyMasterSlaveConnection(masterSlaveDataSource.getConnection(),masterSlaveDataSource.getDataSourceMap());
    }


    @Override
    public final MyMasterSlaveConnection getConnection() {
        //这里替换为自己的数据库连接
        return myMasterSlaveConnection;
    }

    @Override
    protected RuntimeContext getRuntimeContext() {
        return masterSlaveDataSource.getRuntimeContext();
    }
}
