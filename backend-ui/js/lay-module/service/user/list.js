 layui.define(['form', 'table', 'common', "requestUtil", "laytpl"], function(exports) {
     var $ = layui.jquery,
         form = layui.form,
         table = layui.table,
         laytpl = layui.laytpl,
         common = layui.common,
         requestUtil = layui.requestUtil;
     var tablename = table.render({
         elem: '#currentTableId',
         url: requestUtil.UserListURL,
         toolbar: '#toolbarDemo',
         defaultToolbar: ['filter', 'exports', 'print', {
             title: '提示',
             layEvent: 'LAYTABLE_TIPS',
             icon: 'layui-icon-tips'
         }],
         cols: [
             [
                 { type: "checkbox", width: 50 },
                 { field: 'userId', title: 'ID', sort: true },
                 { field: 'username', title: '用户名' },
                 { field: 'nickname', title: '昵称' },
                 { field: 'mobile', title: '手机' },
                 {
                     field: 'roleNameList',
                     title: '角色',
                     templet: function(d) {
                         var getTpl = roleList.innerHTML;
                         var view = "";
                         laytpl(getTpl).render(d, function(html) {
                             view = html;
                         });
                         return view;
                     }
                 },
                 {
                     field: 'status',
                     align: 'center',
                     templet: function(d) {
                         d.disabled = d.username === "admin" || d.username === localStorage.getItem("username") ? true : false;
                         var getTpl = statusTemplate.innerHTML;
						  if(d.disabled == false){
							 d.disabled = !common.hasPermission("user:status");
						  }
                         var view = "";
                         laytpl(getTpl).render(d, function(html) {
                             view = html;
                         });
						 console.log(d.disabled)
                         return view;
                     },
                     title: '状态'
                 },
                 { field: 'createTime', title: '创建时间' },
                 {
                     title: '操作',
                     minWidth: 150,
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
     //监听账号状态
     form.on('switch(switchStatus)', function(data) {
		 console.log(data)
		 console.log(this)
         var status = this.checked ? true : false;
         const msg = status ? "用户正常登录" : "用户禁止登录";
         status = status ? 1 : 0;
         const params = { "userId": data.value, "status": status }
         common.formSubmit(requestUtil.UserStatusURL, params, function(result) {
             if (result.code != 200) {
                 layer.msg(result.msg);
                 return;
             }
             layer.tips("温馨提示：" + msg, data.othis)
         })

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
             common.openWin('../../page/user/edit.html', "添加用户", function(iframeWin) {
                 iframeWin.init({}, false, function() {
                     common.serachTableReload();
                 });
             });
         } else if (obj.event === 'delete') { // 监听删除操作
             var checkStatus = table.checkStatus('currentTableId'),
                 data = checkStatus.data;
             common.batchDeleted(requestUtil.UserDeletdURL, data, "username", "userId", function() {
                 common.serachTableReload(tablename.config.page.curr);
             });

         }
     });

     table.on('tool(currentTableFilter)', function(obj) {
         var data = obj.data;
         if (obj.event === 'edit') {
             common.openWin('../../page/user/edit.html', "编辑用户", function(iframeWin) {
                 iframeWin.init(data, true, function() {
                     common.serachTableReload(tablename.config.page.curr);
                 });
             });
             return false;
         } else if (obj.event === 'delete') {
             var list = [];
             list.push(data);
             common.batchDeleted(requestUtil.UserDeletdURL, list, "username", "userId", function() {
                 common.serachTableReload(tablename.config.page.curr);
             });
         }
     });

 });