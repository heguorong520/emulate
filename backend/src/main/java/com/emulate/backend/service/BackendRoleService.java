

package com.emulate.backend.service;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emulate.backend.dao.BackendRoleDao;
import com.emulate.backend.entity.BackendRoleEntity;
import com.emulate.core.util.PageData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 角色
 */
@Service
public class BackendRoleService extends ServiceImpl<BackendRoleDao, BackendRoleEntity> {
    @Resource
    private BackendRoleMenuService backendRoleMenuService;
    @Resource
    private BackendUserRoleService backendUserRoleService;


    public PageData findPage(Map<String, Object> params) {
        String roleName = (String) params.get("roleName");

        IPage<BackendRoleEntity> page = this.page(
                new Page<>(1, 2),
                new QueryWrapper<BackendRoleEntity>()
                        .like(ObjectUtil.isNotEmpty(roleName), "role_name", roleName));

        return new PageData(page);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveRole(BackendRoleEntity role) {
        role.setCreateTime(new Date());
        this.save(role);

        //保存角色与菜单关系
        backendRoleMenuService.saveOrUpdate(role.getRoleId(), role.getMenuIdList());

    }

    @Transactional(rollbackFor = Exception.class)
    public void update(BackendRoleEntity role) {
        this.updateById(role);

        //更新角色与菜单关系
        backendRoleMenuService.saveOrUpdate(role.getRoleId(), role.getMenuIdList());

    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(Long[] roleIds) {
        //删除角色
        this.removeByIds(Arrays.asList(roleIds));

        //删除角色与菜单关联
        backendRoleMenuService.deleteBatch(roleIds);

        //删除角色与用户关联
        backendUserRoleService.deleteBatch(roleIds);
    }

    public Map<Long, String> findRoleNameMap(List<Long> ids) {
        Map<Long, String> result = new HashMap<>();
        if (ObjectUtil.isEmpty(ids)) {
            return result;
        }
        QueryWrapper<BackendRoleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("role_id", ids);
        List<BackendRoleEntity> roleList = this.baseMapper.selectList(queryWrapper);
        result = roleList.stream().
                collect(
                        Collectors.
                                toMap(
                                        BackendRoleEntity::getRoleId,
                                        BackendRoleEntity::getRoleName
                                )
                );
        return result;
    }
}
