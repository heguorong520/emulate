
package com.emulate.backend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@TableName("backend_user")
public class BackendUserEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    @JsonIgnore
    private String password;

    /**
     * 盐
     */
    @JsonIgnore
    private String salt;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 状态 0：禁用 1：正常
     */
    private Integer status;

    /**
     * 角色ID列表
     */
    @TableField(exist = false)
    private List<Long> roleIdList;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date createTime;

    /**
     * 角色ID列表
     */
    @TableField(exist = false)
    private List<String> roleNameList;

    public List<String> getRoleNameList() {
        if (roleNameList == null) {
            roleNameList = new ArrayList<>();
        }
        return roleNameList;
    }

    public List<Long> getRoleIdList() {
        if (roleIdList == null) {
            roleIdList = new ArrayList<>();
        }
        return roleIdList;
    }

    /**
     * 用户名
     */
    private String nickname;
}
