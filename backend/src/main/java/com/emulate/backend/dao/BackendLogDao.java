package com.emulate.backend.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.emulate.backend.entity.BackendLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统日志
 *
 * @author Mark sunlightcs@gmail.com
 */
@Mapper
public interface BackendLogDao extends BaseMapper<BackendLogEntity> {
	
}
