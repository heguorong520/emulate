layui.define(['table', 'treetable', "requestUtil", "common","laytpl"], function(exports) {
    var $ = layui.jquery;
    var table = layui.table;
    var requestUtil = layui.requestUtil;
    var common = layui.common;
	var laytpl = layui.laytpl;
    var treetable = layui.treetable;
    // 渲染表格
    layer.load(2);
    const params = {
        treeColIndex: 1,
        treeSpid: 0,
        treeIdName: 'menuId',
        treePidName: 'parentId',
        elem: '#currentTable',
        url: requestUtil.MenuListURL,
        toolbar: '#toolbarDemo',
        page: false,
        cols: [
            [{
                type: 'numbers'
            }, {
                field: 'name',
                minWidth: 200,
                title: '权限名称'
            }, {
                field: 'perms',
                title: '权限标识'
            }, {
                field: 'url',
                title: '菜单url'
            }, {
                field: 'shortcut',
                title: '是否快捷菜单',
                templet: function(d) {
                    if (d.shortcut == 1) {
                        return '<span class="layui-badge layui-bg-gray">是</span>';
                    }
                    if (d.shortcut == 0) {
                        return '<span class="layui-badge layui-bg-blue">否</span>';
                    }
                    return "";
                }
            }, {
                field: 'orderNumber',
                width: 80,
                align: 'center',
                title: '排序号'
            }, {
                field: 'ty[e',
                width: 80,
                align: 'center',
                templet: function(d) {
                    if (d.type === 2) {
                        return '<span class="layui-badge layui-bg-gray">按钮</span>';
                    }
                    if (d.type === 0) {
                        return '<span class="layui-badge layui-bg-blue">目录</span>';
                    } else {
                        return '<span class="layui-badge-rim">菜单</span>';
                    }
                },
                title: '类型'
            }, {
                templet:function(d){
					var view = "";
					laytpl(addChildTeamplate.innerHTML).render(d,function(html){
						view = html;
					});
					return view;
				} ,
                width: 200,
                align: 'right',
                title: '操作'
            }]
        ],
        done: function() {
            layer.closeAll('loading');
			 common.buttonPermissions();
        }
    };
    treetable.render(params);



    //监听工具条
    table.on('tool(currentTableFilter)', function(obj) {
        var data = obj.data;
        var layEvent = obj.event;
		console.log(layEvent)
        if (layEvent === 'del') {
         common.deleted(requestUtil.MenuDeletdURL, data, "name", "menuId", function() {
                reloadTreeTable();
         });
        } else if (layEvent === 'edit') {
           common.openWin('../../page/menu/edit.html', "编辑菜单", function(iframeWin) {
               iframeWin.init(data, true, function() {
                   reloadTreeTable();
               });
           });
        }else if (layEvent === 'addChild') {
           common.openWin('../../page/menu/edit.html', "添加下级	", function(iframeWin) {
			   var parent = {parentId:data.menuId}
			   if(data.type ===0){
				   parent.type = 1;
			   }
			   if(data.type === 1){
			   		 parent.type = 2;
			   }
               iframeWin.init(parent, true, function() {
                   reloadTreeTable();
               });
           });
        }
    });

    /**
     * toolbar监听事件
     */
    table.on('toolbar(currentTableFilter)', function(obj) {
        if (obj.event === 'reload') { // 监听添加操作
            reloadTreeTable();
        } else if (obj.event === 'add') { // 监听添加操作
           common.openWin('../../page/menu/edit.html', "添加菜单", function(iframeWin) {
               iframeWin.init({}, false, function() {
                   reloadTreeTable();
               });
           });
        }
    });

    function reloadTreeTable() {
        layer.load(2);
        params.url = requestUtil.MenuListURL;
        params.data = undefined;
        treetable.render(params);
    }
    exports("list", {});
});