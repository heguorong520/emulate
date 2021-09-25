

package com.emulate.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emulate.backend.dao.BackendUserRoleDao;
import com.emulate.backend.entity.BackendUserRoleEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 用户与角色对应关系
 *
 * @author Mark sunlightcs@gmail.com
 */
@Service
public class BackendUserRoleService extends ServiceImpl<BackendUserRoleDao, BackendUserRoleEntity> {


    @Transactional
    public void saveOrUpdate(Long userId, List<Long> roleIdList) {
        //先删除用户与角色关系
        this.remove(new QueryWrapper<BackendUserRoleEntity>().eq("user_id", userId));

        if (roleIdList == null || roleIdList.size() == 0) {
            return;
        }

        //保存用户与角色关系
        for (Long roleId : roleIdList) {
            BackendUserRoleEntity sysUserRoleEntity = new BackendUserRoleEntity();
            sysUserRoleEntity.setUserId(userId);
            sysUserRoleEntity.setRoleId(roleId);

            this.save(sysUserRoleEntity);
        }

    }

    public List<Long> findRoleIdList(Long userId) {
        return baseMapper.queryRoleIdList(userId);
    }

    @Transactional
    public int deleteBatch(Long[] roleIds) {
        return baseMapper.deleteBatch(roleIds);
    }

    public Map<Long, List<Long>> findRoleIdList(Long... userId) {
        if (userId == null || userId.length == 0) {
            return new HashMap<>();
        }
        QueryWrapper<BackendUserRoleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_id", userId);
        List<BackendUserRoleEntity> list = baseMapper.
                selectList(queryWrapper);
        Map<Long, List<Long>> result = list.
                stream().
                map(BackendUserRoleEntity::getUserId).
                collect(Collectors.toList()).
                stream().
                distinct().
                collect(
                        Collectors.toMap(a -> a,
                                id -> list.stream().
                                        filter(userRole -> id.equals(userRole.getUserId())).
                                        map(BackendUserRoleEntity::getRoleId).
                                        collect(Collectors.toList())
                        ));
        return result;
    }

    @Transactional
    public void deleteRoleByUserId(Long... id) {
        if (id == null || id.length == 0){
            return;
        }
        QueryWrapper<BackendUserRoleEntity> queryWrapper =  new QueryWrapper();
        queryWrapper.in("user_id",id);
        baseMapper.delete(queryWrapper);
    }
}
