package com.spider.amap.timer;

import com.spider.amap.service.SpiderMapService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 爬高德小区数据
 *
 * @author zhangjun
 * @date 2018/11/27
 */
@Component
public class SpiderTimer {

    private final static Logger logger = LogManager.getLogger(SpiderTimer.class);
    @Resource
    private SpiderMapService spiderMapService;

    //    @Scheduled(cron = "0 38 20 * * ?")
    @Scheduled(initialDelay = 1000, fixedRate = 60000)
    private void startSpider() {
        spiderMapService.startSpider();
    }


}
