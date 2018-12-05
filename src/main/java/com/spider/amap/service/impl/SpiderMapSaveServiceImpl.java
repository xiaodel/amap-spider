package com.spider.amap.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.spider.amap.service.SpiderMapSaveService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * @author zhangjun
 * @date 2018/12/4
 */
@Service
public class SpiderMapSaveServiceImpl implements SpiderMapSaveService {

    private final static Logger logger = LogManager.getLogger(SpiderMapSaveServiceImpl.class);

    @Override
    public void saveSpiderData(JSONArray array) {
        logger.error(array);
    }
}
