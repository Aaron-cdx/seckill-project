create DATABASE SECKILL;

use seckill;

create table seckill(
`seckill_id` bigint not null auto_increment comment '秒杀id',
`name` varchar(50) not null comment '商品名称',
`number` int(3) not null comment '商品数量',
`start_time` timestamp not null comment '商品创建时间',
`end_time` timestamp not null comment '商品结束时间',
`create_time` timestamp not null default current_timestamp  comment '创建时间',
primary key (`seckill_id`),
key idx_start_time(`start_time`),
key idx_end_time(`end_time`),
key idx_create_time(`create_time`)
)ENGINE=INNODB AUTO_Increment=1000 DEFAULT charset=utf8 comment '商品库存表';



insert into seckill(name,number,start_time,end_time)
values
('1000元秒杀iPhoneXRS',100,'2019-11-29 14:00:00','2019-12-01 14:00:00'),
('800元秒杀一加7',200,'2019-11-29 14:00:00','2019-12-01 14:00:00'),
('500元秒杀小米9',300,'2019-11-29 14:00:00','2019-12-01 14:00:00'),
('300元秒杀红米note8',400,'2019-11-29 14:00:00','2019-12-01 14:00:00');


-- 商品秒杀明细表
-- 利用seckill_id与phone_number设置联合主键
create table success_killed(
`seckill_id` bigint not null comment '秒杀id',
`phone_number` bigint not null comment '用户手机号',
`state` tinyint not null default -1 comment '-1表示无效 0表示秒杀失败 1表示秒杀成功',
`create_time` timestamp not null default current_timestamp comment '创建时间',
primary key(`seckill_id`,`phone_number`),
key idx_create_time(`create_time`)
)ENGINE=INNODB DEFAULT charset=utf8 comment '秒杀成功明细表';