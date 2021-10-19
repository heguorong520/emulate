
package com.emulate.backend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.emulate.backend.entity.BackendMenuEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BackendMenuDao extends BaseMapper<BackendMenuEntity> {

    /**
     * 根据父菜单，查询子菜单
     * @param parentId
     */
    List<BackendMenuEntity> queryListParentId(Long parentId);

    /**
     * 获取不包含按钮的菜单列表
     */
    List<BackendMenuEntity> queryNotButtonList();

}
