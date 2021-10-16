    layui.define(['form', 'common', 'requestUtil'], function(exports) {
        var form = layui.form,
            layer = layui.layer,
            config = layui.requestUtil;
        constUtil = layui.constUtil,
            common = layui.common;
        //jquery = layui.jquery;
        console.log(config)
            // 登录过期的时候，跳出ifram框架
        if (top.location != self.location) top.location = self.location;
        // 粒子线条背景
        $(document).ready(function() {
            $('.layui-container').particleground({
                dotColor: '#7ec7fd',
                lineColor: '#7ec7fd'
            });
        });
        var token = window.localStorage.getItem(constUtil.AUTHORIZATION);
        if (token != null && token != "") {
            window.location = 'index.html';
        }
        // 进行登录操作
        form.on('submit(login)', function(data) {
            data = data.field;
            $.ajax({
                url: config.LoginURL,
                data: JSON.stringify(data),
                type: "post",
                success: function(result) {
                    if (result.code == 200) {
                        layer.msg('登录成功', function() {
                            window.location = 'index.html';
                            window.localStorage.setItem(constUtil.AUTHORIZATION, result.data.token);
                            window.localStorage.setItem(constUtil.LOGIN_USER_NAME, result.data.username);
                            window.localStorage.setItem(constUtil.LOGIN_USER_ID, result.data.userId);
							window.localStorage.setItem(constUtil.PERMS_LIST,result.data.perms);
                        });
                        return;
                    }
					$("#captchaPic").trigger("click");
                    layer.msg(result.msg);
                }
            });
            return false;
        });
        var deviceId = window.localStorage.getItem(constUtil.DEVICEID);
        if (deviceId == "" || deviceId == null) {
            deviceId = common.uuid();
        }
        //验证码
        $("#captchaPic").attr("src", config.CaptchaURL + "?time=" + new Date().getTime()+"&"+constUtil.DEVICEID + "=" + deviceId);
        $(document).on('click', '#captchaPic', function() {
            $(this).attr("src", config.CaptchaURL + "?time=" + new Date().getTime() + "&" + constUtil.DEVICEID + "=" + deviceId);
        });
        exports("login", {});
    });