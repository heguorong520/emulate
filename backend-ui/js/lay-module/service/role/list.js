 layui.define(['form', 'table', 'common', "requestUtil", "laytpl"], function(exports) {
     var $ = layui.jquery,
         form = layui.form,
         table = layui.table,
         laytpl = layui.laytpl,
         common = layui.common,
         requestUtil = layui.requestUtil;
     var tablename = table.render({
         elem: '#currentTableId',
         url: requestUtil.RoleListURL,
         toolbar: '#toolbarDemo',
         defaultToolbar: ['filter', 'exports', 'print', {
             title: '提示',
             layEvent: 'LAYTABLE_TIPS',
             icon: 'layui-icon-tips'
         }],
         cols: [
             [
                 { type: "checkbox", width: 80 },
                 { field: 'roleId', title: 'ID', sort: true, width: 100 },
                 { field: 'roleName', title: '角色名称', width: 120 },
                 { field: 'remark', title: '角色描述', width: 120 },
                 { field: 'userCount', title: '用户数量', width: 100 },
                 { field: 'menuNameList', title: '菜单权限' },
                 { field: 'createTime', title: '创建时间', width: 170 },
                 {
                     title: '操作',
                     width: 150,
                     align: "center",
                     templet: function(d) {
                         if (d.username == "admin") {
                             return "";
                         }
                         return currentTableBar.innerHTML;
                     }
                 },
             ]
         ],
         limits: [10, 15, 20, 25, 50, 100, 1000],
         limit: 15,
         page: true,
         skin: 'line',
		 done:function(){
			 common.buttonPermissions();
		 }
     });

     // 监听搜索操作
     form.on('submit(data-search-btn)', function(data) {
         var result = JSON.stringify(data.field);
         common.serachTableReload(1);
         return false;
     });

     /**
      * toolbar监听事件
      */
     table.on('toolbar(currentTableFilter)', function(obj) {
         if (obj.event === 'add') { // 监听添加操作
             common.openWin('../../page/role/edit.html', "添加用户", function(iframeWin) {
                 iframeWin.init({}, false, function() {
                     common.serachTableReload();
                 });
             });
         } else if (obj.event === 'delete') { // 监听删除操作
             var checkStatus = table.checkStatus('currentTableId'),
                 data = checkStatus.data;
             common.batchDeleted(requestUtil.RoleDeletedURL, data, "roleName", "roleId", function() {
                 common.serachTableReload(tablename.config.page.curr);
             });

         }
     });

     table.on('tool(currentTableFilter)', function(obj) {
         var data = obj.data;
         if (obj.event === 'edit') {
             common.openWin('../../page/role/edit.html', "编辑用户", function(iframeWin) {
                 iframeWin.init(data, true, function() {
                     common.serachTableReload(tablename.config.page.curr);
                 });
             });
             return false;
         } else if (obj.event === 'delete') {
             var list = [];
             list.push(data);
             common.batchDeleted(requestUtil.RoleDeletedURL, list, "roleName", "roleId", function() {
                 common.serachTableReload(tablename.config.page.curr);
             });
         }
     });
     exports("list", {});
 });