package org.seckill.dto;

import java.util.Date;

/**
 * 暴露秒杀接口地址类
 *
 * @author caoduanxi
 * @2019/11/30 11:09
 */
public class Exposer {
    // 是否开启秒杀
    private boolean exposed;

    private long seckillId;

    private String md5;

    private long now;

    private long startTime;

    private long endTime;

    // 判断是否开启秒杀
    public Exposer(boolean exposed, long seckillId) {
        this.exposed = exposed;
        this.seckillId = seckillId;
    }

    // 成功秒杀
    public Exposer(boolean exposed, long seckillId, String md5) {
        this.exposed = exposed;
        this.seckillId = seckillId;
        this.md5 = md5;
    }

    // 失败秒杀，只有成功秒杀才会返回接口地址，失败秒杀不会返回接口地址
    public Exposer(boolean exposed, long seckillId, long now, long startTime, long endTime) {
        this.exposed = exposed;
        this.seckillId = seckillId;
        this.now = now;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public boolean isExposed() {
        return exposed;
    }

    public void setExposed(boolean exposed) {
        this.exposed = exposed;
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public long getNow() {
        return now;
    }

    public void setNow(long now) {
        this.now = now;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Exposer{" +
                "exposed=" + exposed +
                ", seckillId=" + seckillId +
                ", md5='" + md5 + '\'' +
                ", now=" + now +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
