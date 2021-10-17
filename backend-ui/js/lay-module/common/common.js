layui.define(["jquery", 'miniAESUtil', "requestUtil", "table", "form","laytpl"], function(exports) {
    const $ = layui.$;
    const miniAESUtil = layui.miniAESUtil;
    const constUtil = layui.constUtil;
    const table = layui.table;
    const form = layui.form;
	const laytpl = layui.laytpl;
    const common = {
        /**
         * 
         * @param {长度}} len 
         * @param {最大长度} radix 
         * @returns 
         */
        uuid: function(len, radix) {
            var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');
            var uuid = [],
                i;
            radix = radix || chars.length;
            if (len) {
                for (i = 0; i < len; i++) uuid[i] = chars[0 | Math.random() * radix];
            } else {
                var r;
                uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
                uuid[14] = '4';
                if (!uuid[i]) {
                    r = 0 | Math.random() * 16;
                    uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
                }
            }
            return uuid.join('');
        },
        getMonthPamrams: function(url) {
            var theRequest = new Object();
            if (url.indexOf("?") != -1) {
                var str = url.split("?")[1];
                strs = str.split("&");
                for (var i = 0; i < strs.length; i++) {
                    const params = strs[i].split("=");
                    theRequest[params[0]] = unescape(params[1]);
                }
            }
            return theRequest;
        },
        /**
         * 
         * @param {提交地址} url 
         * @param {提交数据} data 
         * @param {回调} callback 
         */
        formSubmit: function(url, data, callback) {
            $.ajax({
                url: url,
                data: JSON.stringify(data),
                type: "post",
                success: function(result) {
                    callback(result);
                },
                error: function() {
                    layer.msg("请求异常");
                }
            });
        },
        /**
         * name=role_1 获取多复选框的值
         * @param {表单数据} data 
         * @param {inpu[name]前缀} namePrefix 
         * @param {分隔符} delimiter 
         * @returns 
         */
        getSelectCheckMultiple: function(data, namePrefix, delimiter) {
            var arr = [];
            Object.keys(data).forEach(function(k) {
                if (k.startsWith(namePrefix)) {
                    arr.push(k.split(delimiter)[1]);
                    delete data[k];
                }
            });
            return arr;
        },

        /**
         * @param {模板} template
         * @param {数据}} data 
         * @param {显示DOM} view 
         * @param {模板插件} laytpl 
         * @param {callback回调} callback 
         */
        setTemplate(template, data, view, callback) {
            var getTpl = template.innerHTML;
            laytpl(getTpl).render(data, function(html) {
                view.innerHTML = html;
                if (callback != undefined) {
                    callback();
                }
            });
        },
        /**
         * 
         * @param {页面地址}} url 
         * @param {标题} title 
         * @param {回调函数} callback 
         */
        openWin: function(url, title, callback) {
            var index = layer.open({
                title: title,
                type: 2,
                shade: 0.2,
                maxmin: true,
                shadeClose: true,
                area: ['100%', '100%'],
                content: url,
                success: function(layero, index) {
                    var iframeWin = window[layero.find('iframe')[0]['name']];
                    if (callback != undefined) {
                        callback(iframeWin);
                    }
                },
            });
            $(window).on("resize", function() {
                layer.full(index);
            });
        },
        batchDeleted: function(url, data, tipsFieldName, deleteByFiled, callback) {
            var str = "";
            const ids = [];
			if(data.length==0){
				layer.msg("请选择需要删除的行");
				return;
			}
            for (var i = 0; i < data.length; i++) {
                if (data[i][tipsFieldName] == "admin") {
                    layer.msg("admin不可删除");
                    return;
                }
				if(data[i]["userCount"] != undefined&&data[i]["userCount"]>0){
					layer.msg("用户数量大于0不可删除角色");
					return;
				}
                str += data[i][tipsFieldName] + ","
                ids.push(data[i][deleteByFiled]);
            }
            layer.confirm('确认删除:' + str.substr(0, str.length - 1), function(index) {
                common.formSubmit(url, ids, function(res) {
                    if (res.code != 200) {
                        layer.msg(res.msg);
                        return;
                    }
                    callback();
                })
                layer.close(index);
            });
        },
		deleted: function(url, data, tipsFieldName, deleteByFiled, callback) {
		    layer.confirm('确认删除:' + data[tipsFieldName], function(index) {
		        $.get(url+"?id="+data[deleteByFiled], function(res) {
		            if (res.code != 200) {
		                layer.msg(res.msg);
		                return;
		            }
		            callback();
		        })
		        layer.close(index);
		    });
		},
        orderParamsStr: function(params) {
            var orderParamsStr = "";
            Object.keys(params).sort().forEach(function(key) {
                if (Array.isArray(params[key]) || params[key] instanceof Object) {
                    orderParamsStr += key + "=" + JSON.stringify(params[key]) + "&";
                } else {
                    console.log(params[key])
                    if (params[key] !== "") {
                        orderParamsStr += key + "=" + params[key] + "&";
                    }
                }
            });
            return orderParamsStr;
        },
        /**
         * 
         * @param {页码} pageNumber 
         */
        serachTableReload: function(pageNumber) {
            var field = form.val("serchForm");
            if (pageNumber == undefined) {
                pageNumber = 1;
                field = {};
            }
            table.reload('currentTableId', {
                page: {
                    curr: pageNumber
                },
                where: field
            }, 'data');
        },
		buttonPermissions: function() {
		
		  $(".permissions").each(function(){
			 var perms = $(this).attr("perms");
			 //在这里做按钮禁用启用
			 if(!common.hasPermission(perms,permsList)){
				 $(this).addClass("layui-hide");
			 }
		  })
		},
		hasPermission:function(perms){
			var permsList = window.localStorage.getItem(constUtil.PERMS_LIST).split(",");
			var userId = window.localStorage.getItem(constUtil.LOGIN_USER_ID);
			 if(userId===1){
				return true;
			 }
			result = false;
			for(var i=0; i<permsList.length; i++){
				if(permsList[i] === perms){
					result = true;
				}
			}
			return result;
		}
    };

    $(document).ajaxSend(function(event, jqxhr, settings) {
        //拦截请求
        var deviceId = window.localStorage.getItem(constUtil.DEVICEID);
        const time = new Date().getTime();
        const random = common.uuid(32, 12)
        const authorization = window.localStorage.getItem(constUtil.AUTHORIZATION);
        if (deviceId == null || deviceId == "") {
            deviceId = common.uuid(32, 32);
            window.localStorage.setItem(constUtil.DEVICEID, deviceId);
        }
        var params = {};
        if (settings.type == "post" || settings.type == "POST") {
            if (settings.data != null && settings.data != undefined && settings.data != "") { //[body]加签只接受数组和对象
                if (settings.data.startsWith("[") || settings.data.startsWith("{")) {
                    params = JSON.parse(settings.data);
                }
            }
        } else {
            params = common.getMonthPamrams(settings.url);
        }
        jqxhr.setRequestHeader(constUtil.CLIENT_TYPE, constUtil.CLIENT_TYPE_VALUE);
        jqxhr.setRequestHeader(constUtil.VERSION, constUtil.VERSION_VALUE);
        jqxhr.setRequestHeader(constUtil.DEVICEID, deviceId);
        jqxhr.setRequestHeader(constUtil.TIME, time);
        jqxhr.setRequestHeader(constUtil.RANDOM, random);
        jqxhr.setRequestHeader(constUtil.AUTHORIZATION, authorization);
        params[constUtil.CLIENT_TYPE] = constUtil.CLIENT_TYPE_VALUE;
        params[constUtil.VERSION] = constUtil.VERSION_VALUE;
        params[constUtil.DEVICEID] = deviceId;
        params[constUtil.TIME] = time;
        params[constUtil.RANDOM] = random;
        var orderParamsStr = common.orderParamsStr(params);
        jqxhr.setRequestHeader("content-type", "application/json");
        orderParamsStr += constUtil.KEY_VALUE;
        jqxhr.setRequestHeader(constUtil.SIGN, miniAESUtil.encrypt(orderParamsStr));
    });

    $.ajaxSetup({
        contentType: "application/json;charset=utf-8",
        complete: function(XMLHttpRequest, textStatus) {
            //通过XMLHttpRequest取得响应结果
            if (XMLHttpRequest.responseText != null && XMLHttpRequest.responseText != "") {
                try {
                    var obj = eval('(' + XMLHttpRequest.responseText + ')');
                    XMLHttpRequest.responseJSON = obj;
                    console.log(XMLHttpRequest)
                    if (obj.code == 401) {
                        window.localStorage.setItem(constUtil.AUTHORIZATION, "")
                        window.parent.location = GetUrlRelativePath() +'/login.html';
                    }
                } catch (e) {
                    console.log("page");
                }
            }
        }
    });
	　function GetUrlRelativePath(){
　　　　var url = document.location.toString();
　　　　var arrUrl = url.split("//");
　　　　var start = arrUrl[1].split("/");
　　　　return arrUrl[0]+"//"+start[0]+"/"+start[1]+"/"+start[2];
　　	 }
    exports("common", common);
});