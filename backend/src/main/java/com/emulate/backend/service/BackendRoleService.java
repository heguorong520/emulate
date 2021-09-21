 

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
import java.util.Arrays;
import java.util.Date;
import java.util.Map;


/**
 * 角色
 *
 */
@Service
public class BackendRoleService extends ServiceImpl<BackendRoleDao, BackendRoleEntity> {
	@Resource
	private BackendRoleMenuService backendRoleMenuService;
	@Resource
	private BackendUserRoleService backendUserRoleService;


	public PageData queryPage(Map<String, Object> params) {
		String roleName = (String)params.get("roleName");

		IPage<BackendRoleEntity> page = this.page(
			new Page<>(1,2),
			new QueryWrapper<BackendRoleEntity>()
				.like(ObjectUtil.isNotEmpty(roleName),"role_name", roleName));

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


}
