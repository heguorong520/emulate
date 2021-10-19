
package com.emulate.backend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.emulate.backend.entity.BackendUserRoleEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BackendUserRoleDao extends BaseMapper<BackendUserRoleEntity> {

    /**
     * 根据用户ID，获取角色ID列表
     */
    List<Long> queryRoleIdList(Long userId);

    /**
     * 根据角色ID数组，批量删除
     */
    int deleteBatch(Long[] roleIds);
}
