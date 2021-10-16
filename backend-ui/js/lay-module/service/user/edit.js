function init(data, edit, callback) {
    layui.use(['form', 'common', "requestUtil", "laytpl", "miniFormVerify"], function() {
        var form = layui.form,
            layer = layui.layer,
            laytpl = layui.laytpl,
            common = layui.common,
            requestUtil = layui.requestUtil,
            $ = layui.$;
        $(function() {
            //查询当前登录用户的角色
            $.get(requestUtil.RoleSelectURL, function(res) {
                if (res.code == 200) {
                    var view = document.getElementById('roleList');
                    common.setTemplate(roleTemplate, res, view, function() {
                        //修改接口
                        if (edit) {
                            form.val("userForm", data);
                        }
                        form.render();
                    })
                }
            });
            //监听保存
            form.on('submit(saveBtn)', function(data) {
                data = data.field;
                common.formSubmit(requestUtil.UserSaveURL, data, function(result) {
                    if (result.code != 200) {
                        layer.msg(result.msg);
                        return;
                    }
                    layer.msg(result.msg, function() {
                        callback();
                        var iframeIndex = parent.layer.getFrameIndex(window.name);
                        parent.layer.close(iframeIndex);
                    }, 50);
                }) 
                return false;
            });
            if (edit) {
                $("input[name='username']").addClass("layui-disabled").prop("disabled", true);
            }
        });
    });
}