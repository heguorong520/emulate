function init(data, edit, callback) {
    layui.use(['form', 'common', "requestUtil", "laytpl", "miniFormVerify", "authtree","iconPickerFa"], function() {
        var form = layui.form,
            layer = layui.layer,
            laytpl = layui.laytpl,
            common = layui.common,
            authtree = layui.authtree,
            requestUtil = layui.requestUtil,
			iconPickerFa = layui.iconPickerFa,
            $ = layui.$;
        $(function() {
            // //查询所有可分配菜单
            $.get(requestUtil.MenuSelectList, function(res) {
				if(res.code != 200){
					layer.msg("上级资源加载失败!");
					return;
				}
				loadTree(res.data);
				if(edit){
					form.val("menuForm",data);
				}
            });
			
			function loadTree(tree){
				authtree.render('#LAY-auth-tree-index', tree, {
				     inputname: 'parentId'
								//,checkedValueList:data.menuIdList
				    ,layfilter: 'lay-check-auth'
									 ,valueKey: "menuId"
									 ,nameKey:"name"
									 ,openall:true
									 ,childKey:"child"
									 ,checkType: 'radio'
				    ,'theme': 'auth-skin-default'
					,checkSkin: 'primary'
				    ,autowidth: true
				    ,formFilter: 'menuForm' // 注意！！！如果不与其他插件render冲突，这个选填
				  });
			}
			
			form.on('radio(filter-type)', function(data){ 
				if(data.value == 1){
					$("#menuName").html("菜单名称");
				} 
				if(data.value == 2){
					$("#menuName").html("按钮名称");
					console.log(data);
				} 
				if(data.value == 0){
					$("#menuName").html("目录名称");
					console.log(data);
				} 
			});
            //监听保存
			iconPickerFa.render({
			    // 选择器，推荐使用input
			    elem: '#iconPicker',
			    // fa 图标接口
			    url: "../../lib/font-awesome-4.7.0/less/variables.less",
			    // 是否开启搜索：true/false，默认true
			    search: true,
			    // 是否开启分页：true/false，默认true
			    page: true,
			    // 每页显示数量，默认12
			    limit: 20,
			    // 点击回调
			    click: function (data) {
			        console.log(data);
			    },
			    // 渲染成功后的回调
			    success: function (d) {
			        console.log(d);
			    }
			});
			
            form.render()
            form.on('submit(saveBtn)', function(data) {
                data = data.field;
                common.formSubmit(requestUtil.MenuSaveURL, data, function(result) {
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
        });
    });
}