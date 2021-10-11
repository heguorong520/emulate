package com.emulate.database.sharding;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.MasterSlaveDataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Slf4j
@Component
public class MyMasterSlaveDatasourceInitializationProcess implements BeanPostProcessor {

    /**
     * 在MasterSlaveDataSourceBean创建的时候替换成我们自己的对象
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof MasterSlaveDataSource) {
            MasterSlaveDataSource masterSlaveDataSource = (MasterSlaveDataSource) bean;
            try {
                MyMasterSlaveDataSource myMasterSlaveDataSource = new MyMasterSlaveDataSource(masterSlaveDataSource);
                log.info("替换MyMasterSlaveDataSource成功");
                return  myMasterSlaveDataSource;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                log.info("创建MyMasterSlaveDataSource失败{}",throwables);
            }

        }
        return bean;
    }
}
