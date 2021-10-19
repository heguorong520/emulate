
package com.emulate.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emulate.backend.dao.BackendRoleMenuDao;
import com.emulate.backend.entity.BackendRoleMenuEntity;
import com.emulate.backend.entity.BackendUserRoleEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class BackendRoleMenuService extends ServiceImpl<BackendRoleMenuDao, BackendRoleMenuEntity> {

    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(Long roleId, List<Long> menuIdList) {
        // 先删除角色与菜单关系
        deleteBatch(new Long[] {roleId});

        if (menuIdList.size() == 0) {
            return;
        }

        // 保存角色与菜单关系
        for (Long menuId : menuIdList) {
            BackendRoleMenuEntity sysRoleMenuEntity = new BackendRoleMenuEntity();
            sysRoleMenuEntity.setMenuId(menuId);
            sysRoleMenuEntity.setRoleId(roleId);

            this.save(sysRoleMenuEntity);
        }
    }

    public List<Long> findMenuIdList(Long roleId) {
        return baseMapper.queryMenuIdList(roleId);
    }

    public int deleteBatch(Long[] roleIds) {
        return baseMapper.deleteBatch(roleIds);
    }

    public Map<Long, List<Long>> findMenuIdList(Long... roleId) {
        if (roleId == null || roleId.length == 0) {
            return new HashMap<>();
        }
        QueryWrapper<BackendRoleMenuEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("role_id", roleId);
        List<BackendRoleMenuEntity> list = baseMapper.selectList(queryWrapper);
        Map<Long,
            List<Long>> result = list.stream().map(BackendRoleMenuEntity::getRoleId).collect(Collectors.toList())
                .stream().distinct().collect(
                    Collectors.toMap(a -> a, id -> list.stream().filter(userRole -> id.equals(userRole.getRoleId()))
                        .map(BackendRoleMenuEntity::getMenuId).collect(Collectors.toList())));
        return result;
    }
}
