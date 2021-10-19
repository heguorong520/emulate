
package com.emulate.backend.controller;

import com.emulate.backend.dto.BackendUpdatePwdDTO;
import com.emulate.backend.dto.BackendUserDTO;
import com.emulate.backend.dto.BackendUserStatusDTO;
import com.emulate.backend.dto.QueryUserDTO;
import com.emulate.backend.service.BackendUserRoleService;
import com.emulate.backend.service.BackendUserService;
import com.emulate.core.controller.BaseApiController;
import com.emulate.core.excetion.CustomizeException;
import com.emulate.core.result.ResultBody;
import com.emulate.database.page.PageData;
import com.emulate.permissions.annotation.Permissions;
import com.emulate.permissions.util.PermissionsUserUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 系统用户
 */
@Api("用户管理员")
@RestController
public class UserController extends BaseApiController {
    @Autowired
    private BackendUserService backendUserService;
    @Autowired
    private BackendUserRoleService backendUserRoleService;

    @ApiOperation("用户列表")
    @Permissions(perms = "user:list")
    @GetMapping("user/list")
    public ResultBody<?> list(@ModelAttribute QueryUserDTO queryUserDTO) {
        PageData page = backendUserService.findPage(queryUserDTO);

        return ResultBody.ok(page);
    }

    @Permissions(perms = "user:password")
    @ApiOperation("修改密码")
    @PostMapping("user/password")
    public ResultBody<?> password(@RequestBody @Valid BackendUpdatePwdDTO backendUpdatePwdDTO) {
        // 更新密码
        boolean flag = backendUserService.updatePassword(PermissionsUserUtil.getUserId(),
            backendUpdatePwdDTO.getPassword(), backendUpdatePwdDTO.getNewPassword());
        if (!flag) {
            throw new CustomizeException("原密码不正确");
        }

        return ResultBody.ok();
    }

    @Permissions(perms = "user:save")
    @ApiOperation("保存用户")
    @PostMapping("user/save")
    public ResultBody<?> save(@Valid @RequestBody BackendUserDTO user) {

        backendUserService.saveUser(user);

        return ResultBody.ok();
    }

    @Permissions(perms = "user:save")
    @ApiOperation("删除用户")
    @PostMapping("user/delete")
    public ResultBody<?> delete(@RequestBody Long[] userIds) {
        if (ArrayUtils.contains(userIds, 1L)) {
            throw new CustomizeException("系统内置管理员不能删除");
        }

        if (ArrayUtils.contains(userIds, PermissionsUserUtil.getUserId())) {
            throw new CustomizeException("当前用户不能删除");
        }
        backendUserService.deleteByUserId(userIds);
        return ResultBody.ok();
    }

    @ApiOperation("禁用启用")
    @Permissions(perms = "user:status")
    @PostMapping("user/status")
    public ResultBody<?> status(@Valid @RequestBody BackendUserStatusDTO userStatusDTO) {
        backendUserService.setUserStatus(userStatusDTO);
        return ResultBody.ok();
    }
}
