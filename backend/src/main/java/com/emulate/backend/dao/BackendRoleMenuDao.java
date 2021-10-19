
package com.emulate.backend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.emulate.backend.entity.BackendRoleMenuEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 角色与菜单对应关系
 */
@Mapper
public interface BackendRoleMenuDao extends BaseMapper<BackendRoleMenuEntity> {

    /**
     * 根据角色ID，获取菜单ID列表
     */
    List<Long> queryMenuIdList(Long roleId);

    /**
     * 根据角色ID数组，批量删除
     */
    int deleteBatch(Long[] roleIds);
}
