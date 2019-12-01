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


-- 使用存储过程优化sql的执行
-- 使用存储过程执行秒杀

DELIMITER $$ -- console ; 转换为 $$

create procedure `seckill`.`execute_seckill`
(
    IN v_seckill_id bigint, IN v_phone bigint, IN v_kill_time timestamp, out r_result INT
)
BEGIN
DECLARE
    insert_count int default 0;
start transaction;
insert ignore into success_killed(seckill_id,user_phone,create_time)
values
(v_seckill_id,v_phone,v_kill_time);
select
    ROW_COUNT() into insert_count;
if(insert_count = 0) then rollback;
set r_result = -1;
elseif(insert_count < 0) then rollback;
set r_result = -2;
else
    update seckill
    set number = number - 1
    where seckill_id = v_seckill_id
    and end_time > v_kill_time
    and start_time < v_kill_time and number > 0;
select ROW_COUNT() into insert_count;
if(insert_count = 0) then
    rollback;
    set r_result = 0;
elseif(insert_count < 0) then
    rollback;
    set r_result = -2;
else commit; set r_result = 1;
end if;
end if;
end;
$$
-- 存储过程定义结束
DELIMITER ;
set @r_result = -3;
call execute_seckill(1001,13232349988,now(),@r_result);
select @r_result;

-- 存储过程
-- 1.存储过程优化，事务行级锁持有时间
-- 2.不要过度依赖存储过程
-- 3.简单的逻辑可以应用存储过程
-- 4.QPS：当前的秒杀单一个6000/QPS


