

package com.emulate.backend.controller;


import com.emulate.backend.dto.BackendUpdatePwdDTO;
import com.emulate.backend.dto.BackendUserDTO;
import com.emulate.backend.dto.BackendUserStatusDTO;
import com.emulate.backend.dto.QueryUserDTO;
import com.emulate.backend.entity.BackendUserEntity;
import com.emulate.backend.service.BackendUserRoleService;
import com.emulate.backend.service.BackendUserService;
import com.emulate.core.controller.BaseController;
import com.emulate.core.excetion.CustomizeException;
import com.emulate.core.filter.AuthFilter;
import com.emulate.core.result.ResultBody;
import com.emulate.core.util.PageData;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

/**
 * 系统用户
 */
@RestController
public class UserController extends BaseController {
    @Autowired
    private BackendUserService backendUserService;
    @Autowired
    private BackendUserRoleService backendUserRoleService;

    @GetMapping("user/list")
    public ResultBody<?> list(@ModelAttribute QueryUserDTO queryUserDTO) {
        PageData page = backendUserService.findPage(queryUserDTO);

        return ResultBody.ok(page);
    }


    @PostMapping("user/password")
    public ResultBody<?> password(@RequestBody @Valid BackendUpdatePwdDTO backendUpdatePwdDTO) throws Exception {
        //更新密码
        boolean flag = backendUserService.updatePassword(AuthFilter.backendLoginUserDTO().getUserId(), backendUpdatePwdDTO.getPassword(), backendUpdatePwdDTO.getNewPassword());
        if (!flag) {
            throw new CustomizeException("原密码不正确");
        }

        return ResultBody.ok();
    }

    @GetMapping("user/info")
    public ResultBody<?> info(Long userId) {
        BackendUserEntity user = backendUserService.getById(userId);

        //获取用户所属的角色列表
        List<Long> roleIdList = backendUserRoleService.findRoleIdList(userId);
        user.setRoleIdList(roleIdList);

        return ResultBody.ok(user);
    }

    @PostMapping("user/save")
    public ResultBody<?> save(@Valid @RequestBody BackendUserDTO user) throws Exception {

        backendUserService.saveUser(user);

        return ResultBody.ok();
    }

    @PostMapping("user/delete")
    public ResultBody<?> delete(@RequestBody Long[] userIds) {
        if (ArrayUtils.contains(userIds, 1L)) {
            throw new CustomizeException("系统内置管理员不能删除");
        }

        if (ArrayUtils.contains(userIds, AuthFilter.backendLoginUserDTO().getUserId())) {
            throw new CustomizeException("当前用户不能删除");
        }
        backendUserService.deleteByUserId(userIds);
        return ResultBody.ok();
    }

    @PostMapping("user/status")
    public ResultBody<?> status(@Valid @RequestBody BackendUserStatusDTO userStatusDTO) {
        backendUserService.setUserStatus(userStatusDTO);
        return ResultBody.ok();
    }
}
