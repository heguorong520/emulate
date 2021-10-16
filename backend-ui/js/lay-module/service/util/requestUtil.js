    /**
     * 定义接口地址
     * @param {Object} exports
     */
    layui.define([], function(exports) {
        const base = "http://127.0.0.1:8084";
        const requestUtil = {
            baseURL: base,
            LoginURL: base + "/backend/api/a/login", //登录
            CaptchaURL: base + "/backend/api/sa/captcha.jpg?", //图像
            LogoutURL: base + "/backend/api/logout", //登出
            LoginInfoURL: base + "/backend/api/login/info", //获取当前用户信息
            UserListURL: base + "/backend/api/user/list",
            NavURL: base + "/backend/api/menu/nav", //导航
            RoleSelectURL: base + "/backend/api/role/select", //角色列表
            UserSaveURL: base + "/backend/api/user/save", //添加用户
            UserDeletdURL: base + "/backend/api/user/delete", //删除用户
            UserStatusURL: base + "/backend/api/user/status", //禁用启用
            RoleListURL: base + "/backend/api/role/list",
            RoleSaveURL: base + "/backend/api/role/save",
            RoleDeletedURL: base + "/backend/api/role/delete",
			RoleMenuSelectURL : base + "/backend/api/menu/role/select",
            MenuListURL: base + "/backend/api/menu/list",
            MenuSelectList: base + "/backend/api/menu/select",
			MenuSaveURL: base + "/backend/api/menu/save",
			MenuDeletdURL : base + "/backend/api/menu/delete"
        }
        exports("requestUtil", requestUtil);
    });