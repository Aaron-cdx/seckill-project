package org.seckill.web;

import org.seckill.dto.ExecutionSeckill;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecutionResult;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatException;
import org.seckill.exception.SeckillEndException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author caoduanxi
 * @2019/12/1 9:45
 */
@Controller
@RequestMapping("/seckill")
public class SeckillController {
    private static final Logger logger = LoggerFactory.getLogger(SeckillController.class);
    @Autowired
    private SeckillService seckillService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model) {
        List<Seckill> list = seckillService.getSeckillList();
        model.addAttribute("list", list);
        return "list";
    }

    /**
     * 详情页
     *
     * @param seckillId
     * @return
     */
    @RequestMapping(value = "/seckill/{seckillId}/detail", method = RequestMethod.GET)
    public String getById(@PathVariable("seckillId") Long seckillId, Model model) {
        if (seckillId == null) {
            return "redirect:/seckill/list";
        }
        Seckill seckill = seckillService.getById(seckillId);
        if (seckill == null) {
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill", seckill);
        return "detail";
    }

    // 获取秒杀接口地址
    // 这里需要直接返回一个具体的json对象，需要使用ResponseBody来将数据返回格式限定
    @RequestMapping(value = "/seckill/{seckillId}/exposer", method = RequestMethod.POST,
            produces = {"application/json;charset=utf-8"})
    @ResponseBody
    public SeckillExecutionResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId) {
        SeckillExecutionResult<Exposer> result;
        // 这个方法根本就没有抛出异常，即使出现了异常，这里根本捕捉不到
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        // 如果开启秒杀
        if (exposer.isExposed()) {
            if (exposer.getMd5() != null) {
                result = new SeckillExecutionResult<Exposer>(true, exposer);
            } else {
                result = new SeckillExecutionResult<Exposer>(false, "数据被篡改");
            }
        } else {
            result = new SeckillExecutionResult<Exposer>(false, "未开启秒杀！");
        }
        return result;
    }

    // 注意cookie中获取的killPhone需要设置为false，否则一旦没有值，springMVC就会报错
    @RequestMapping(value = "/seckill/{seckillId}/{md5}/execution", method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillExecutionResult<ExecutionSeckill> execute(@PathVariable("seckillId") Long seckillId,
                                                            @PathVariable("md5") String md5,
                                                            @CookieValue(value = "killPhone", required = false) Long phone) {
        SeckillExecutionResult<ExecutionSeckill> result;
        if (phone == null) {
            return new SeckillExecutionResult<>(false, "当前用户未注册！");
        }
        try {
            ExecutionSeckill executionSeckill = seckillService.executeSeckill(seckillId, phone, md5);
            result = new SeckillExecutionResult<>(true, executionSeckill);
        } catch (RepeatException e) {
            ExecutionSeckill executionSeckill = new ExecutionSeckill(seckillId, SeckillStateEnum.REPEAT_KILL);
            result = new SeckillExecutionResult<>(true, executionSeckill);
        } catch (SeckillEndException e) {
            ExecutionSeckill executionSeckill = new ExecutionSeckill(seckillId, SeckillStateEnum.END);
            result = new SeckillExecutionResult<>(true, executionSeckill);
        } catch (SeckillException e) {
            logger.error(e.getMessage());
            ExecutionSeckill executionSeckill = new ExecutionSeckill(seckillId, SeckillStateEnum.INNER_ERROR);
            result = new SeckillExecutionResult<>(true, executionSeckill);
        }
        return result;
    }

    // 获取系统当前时间
    @RequestMapping(value = "/seckill/time/now", method = RequestMethod.GET)
    @ResponseBody
    public SeckillExecutionResult<Long> getTime() {
        Date now = new Date();
        return new SeckillExecutionResult<>(true, now.getTime());
    }
}
