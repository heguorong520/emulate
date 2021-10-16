/**
 * date:2020/03/01
 * author:Mr.Chung
 * version:2.0
 * description:layuimini 统计框架扩展
 */
layui.define(["form","jquery"], function(exports) {
    var form = layui.form
	$ = layui.jquery;

    form.verify({
        specialChar: function(value) {
            var reg = /[^\w\u4e00-\u9fa5\/\-]/gi;
            if (reg.test(value)) {
                //return '不能包含特殊字符！'
            }
        },
        maxLeng50: function(value) {
            if (value.length > 50) {
                return '不能超过50个字符！';
            }
        },
        maxLeng100: function(value) {
            if (value.length > 50) {
                return '不能超过50个字符！';
            }
        },
		maxLeng200: function(value) {
		    if (value.length > 200) {
		        return '不能超过50个字符！';
		    }
		},
        minLeng5: function(value) {
            if (value.length < 5) {
                return '至少得5个字符！';
            }
        },
        minLeng10: function(value) {
            if (value.length < 10) {
                return '至少得5个字符！';
            }
        },
        checkbox: function(value, item) {
            if ($(item).children(".layui-form-checked").length == 0) {
                return "至少选一项";
            }
        }
    });
    exports("miniFormVerify", {});
});