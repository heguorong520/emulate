

package com.emulate.backend.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emulate.backend.dao.BackendRoleDao;
import com.emulate.backend.dto.BackendRoleDTO;
import com.emulate.backend.dto.QueryRoleDTO;
import com.emulate.backend.entity.BackendRoleEntity;
import com.emulate.database.page.PageData;
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

    @Resource
    private BackendMenuService backendMenuService;

    public PageData findPage(QueryRoleDTO queryRoleDTO) {
        IPage<BackendRoleEntity> page = this.page(
                new Page<>(queryRoleDTO.getPage(), queryRoleDTO.getLimit()),
                new QueryWrapper<BackendRoleEntity>()
                        .like(ObjectUtil.isNotEmpty(queryRoleDTO.getRoleName()), "role_name", queryRoleDTO.getRoleName()));
        //查询角色对应的菜单
        if (page.getRecords().size() > 0) {
            List<Long> roleIds = page.getRecords().
                    stream().
                    map(BackendRoleEntity::getRoleId).
                    collect(Collectors.toList());
            //获取角色对应菜单ID
            Map<Long, List<Long>> userMenuIdMaps = backendRoleMenuService.findMenuIdList(roleIds.toArray(new Long[roleIds.size() - 1]));
            List<Long> menuIds = new ArrayList<>();
            userMenuIdMaps.forEach((k, v) -> {
                menuIds.addAll(v);
            });
            //菜单名称填充处理
            Map<Long, String> MenuNameMap = backendMenuService.findMenuNameMap(menuIds);

            //查询角色对象用户
            Map<Long, Integer> userCountMaps = backendUserRoleService.findRoleUserCountMap(roleIds);
            for (BackendRoleEntity u : page.getRecords()) {
                u.setUserCount(userCountMaps.getOrDefault(u.getRoleId(), 0));
                if (!userMenuIdMaps.containsKey(u.getRoleId())) {
                    continue;
                }
                //查询所有菜单名称
                List<Long> roleMenuIds = userMenuIdMaps.get(u.getRoleId());
                for (Long menuId : roleMenuIds) {
                    if (!MenuNameMap.containsKey(menuId)) {
                        continue;
                    }
                    u.getMenuNameList().add(MenuNameMap.get(menuId));
                    u.getMenuIdList().add(menuId);
                }
            }
        }
        return new PageData(page);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveRole(BackendRoleDTO roleDTO) {
        BackendRoleEntity role = BeanUtil.toBean(roleDTO, BackendRoleEntity.class);
        this.saveOrUpdate(role);
        //保存角色与菜单关系
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
