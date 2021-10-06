 

package com.emulate.backend.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 角色
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
@TableName("backend_role")
public class BackendRoleEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 角色ID
	 */
	@TableId
	private Long roleId;

	/**
	 * 角色名称
	 */
	@NotBlank(message = "角色名称不能为空")
	private String roleName;

	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 部门ID
	 */
	private Long deptId;



	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;
	/**
	 * 部门名称
	 */
	@TableField(exist=false)
	private String deptName;

	@TableField(exist=false)
	private List<Long> menuIdList;

	@TableField(exist=false)
	private List<String> menuNameList;

	@TableField(exist=false)
	private List<Long> deptIdList;
	@TableField(exist=false)
	public Integer userCount=0;

	public List<String> getMenuNameList(){
		if(menuNameList == null){
			menuNameList = new ArrayList<>();
		}
		return menuNameList;
	}
	public List<Long> getMenuIdList(){
		if(menuIdList == null){
			menuIdList = new ArrayList<>();
		}
		return menuIdList;
	}
}
