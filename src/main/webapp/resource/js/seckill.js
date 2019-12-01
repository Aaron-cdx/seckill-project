//代码封装
//javascript模块化

var seckill = {
    //封装秒杀的ajax相关的url地址
    URL: {
        now: '/seckill/seckill/time/now',
        exposer: function (seckillId) {
            return '/seckill/seckill/' + seckillId + '/exposer';
        },
        execution: function (seckillId, md5) {
            return '/seckill/seckill/' + seckillId + '/' + md5 + '/execution';
        }
    },
    validatePhone: function (phone) {
        if (phone && phone.length == 11 && !isNaN(phone)) {
            return true;
        } else {
            return false;
        }
    },
    handlerSeckillkill: function (seckillId, node) {
        node.hide()
            .html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');
        $.post(seckill.URL.exposer(seckillId), {}, function (result) {
            //暴露秒杀接口地址
            if (result && result['success']) {
                //获取到暴露的接口地址数据
                var exposer = result['data'];
                var md5 = exposer['md5'];
                var killUrl = seckill.URL.execution(seckillId, md5);
                //可以执行秒杀了
                if (exposer['exposed']) {
                    //按键的时间绑定
                    $('#killBtn').one('click', function () {
                        $(this).addClass('disabled');             //按键失效
                        $.post(killUrl, {}, function (result) {
                            if (result && result['success']) {
                                //获取秒杀的数据
                                var execute = result['data'];
                                //获取秒杀的状态
                                var state = execute['state'];
                                //获取秒杀的信息
                                var stateInfo = execute['stateInfo'];
                                //显示秒杀结果
                                node.html('<span class="label label-success">' + stateInfo + '</span>');
                            }
                        });
                    });
                } else {
                    //不能执行秒杀获取系统当前时间 重新走到倒计时界面
                    var now = exposer['now'];
                    var start = exposer['start'];
                    var end = exposer['end'];
                    seckill.countDown(seckillId, now, start, end);
                }
                node.show();
            } else {
                console.log("result:" + result);
            }
        });

    },

    countDown: function (seckillId, nowTime, startTime, endTime) {
        //根据时间来验证
        var seckillBox = $('#seckill-box');
        if (nowTime > endTime) {
            seckillBox.html('秒杀结束！');
        } else if (nowTime < startTime) {
//			seckillBox.html('秒杀未开始！');
            //启动倒计时 计时事件绑定
            var killTime = new Date(startTime + 1000);
            seckillBox.countdown(killTime, function (event) {
                //时间格式
                var format = event.strftime("秒杀倒计时：%D天  %H时  %M分  %S秒");
                seckillBox.html(format);
            }).on('finished.countdown', function () {
                //获取秒杀地址 实现现实逻辑
                seckill.handlerSeckillkill(seckillId, seckillBox);

            });
        } else {
            //秒杀开始
            seckill.handlerSeckillkill(seckillId, seckillBox);
        }
    },
    //详情页封装
    detail: {
        //初始化
        init: function (params) {
            //手机验证和登录 交互
            //规划交互流程
            //验证手机号
            //在cookie中获取手机号
            var killPhone = $.cookie('killPhone');

            //开始验证手机号
            //验证不通过
            if (!seckill.validatePhone(killPhone)) {
                //提醒用户注册
                //绑定phone
                var killPhoneModal = $('#killPhoneModal');
                killPhoneModal.modal({
                    show: true,//显示弹出层
                    dropback: 'static',//禁止拖拽事件
                    keyboard: false//关闭键盘事件
                });
                //点击提交按钮的时候，触发此函数
                $('#killPhoneBtn').click(function () {
                    var inputPhone = $('#killPhoneKey').val();
                    //在验证一遍
                    if (seckill.validatePhone(inputPhone)) {
                        //可以查看详情页
                        //将其写进cookie
                        $.cookie('killPhone', inputPhone, {expires: 7, path: '/seckill'});
                        //页面重新刷新就好
                        window.location.reload();
                    } else {
                        $('#killPhoneMessage').hide().html('<label class="text-danger">手机号错误！</label>').show(300);
                    }
                });
            }
            //已经登录
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            $.get(seckill.URL.now, {}, function (result) {
                //result['success']是time中的一个封装好的success为true才有data
                if (result && result['success']) {
                    var nowTime = result['data'];
                    seckill.countDown(seckillId, nowTime, startTime, endTime);
                } else {
                    console.log("result:" + result);
                }

            });

        }
    }
};