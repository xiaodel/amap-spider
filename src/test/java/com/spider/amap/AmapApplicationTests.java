package com.spider.amap;

import com.spider.amap.service.SpiderMapService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AmapApplicationTests {

    @Resource
    private SpiderMapService spiderMapService;

    @Test
    public void contextLoads() {
        spiderMapService.startSpider();
    }

}
