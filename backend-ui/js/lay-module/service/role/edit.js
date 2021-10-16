function init(data, edit, callback) {
	console.log(data)
    layui.use(['form', 'common', "requestUtil", "laytpl", "miniFormVerify","authtree"], function() {
        var form = layui.form,
            layer = layui.layer,
            laytpl = layui.laytpl,
            common = layui.common,
			authtree = layui.authtree,
            requestUtil = layui.requestUtil,
            $ = layui.$;
        $(function() {
            //查询所有可分配菜单
            $.get(requestUtil.RoleMenuSelectURL, function(res) {
                 authtree.render('#LAY-auth-tree-index', res.data, {
                      inputname: 'menuIdList'
					,checkedValueList:data.menuIdList
                     ,layfilter: 'lay-check-auth'
					 ,valueKey: "menuId"
					 ,nameKey:"name"
					 ,openall:true
					 ,childKey:"child"
                     ,'theme': 'auth-skin-default'
                     //,'themePath': '../..//jstree/themes/'
                     // ,collapseLastDepthNode: false
					 ,checkSkin: 'primary'
                     ,autowidth: true
                     ,formFilter: 'roleForm' // 注意！！！如果不与其他插件render冲突，这个选填
                   });
				   //修改接口
				   if (edit) {
				       form.val("roleForm", data);
				   }
				   form.render()
            });
            //监听保存
            form.on('submit(saveBtn)', function(data) {
               data = data.field;
				console.log(data);
               // data.roleIdList = common.getSelectCheckMultiple(data, "role_", "_");
                 common.formSubmit(requestUtil.RoleSaveURL, data, function(result) {
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