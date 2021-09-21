

package com.emulate.backend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.emulate.backend.entity.BackendRoleEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色管理
 */
@Mapper
public interface BackendRoleDao extends BaseMapper<BackendRoleEntity> {
	

}
