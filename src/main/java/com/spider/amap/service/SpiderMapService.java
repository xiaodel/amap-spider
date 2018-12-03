package com.spider.amap.service;

import com.spider.amap.config.MapConfiguration;
import com.spider.amap.enums.EmailTypeEnum;
import com.spider.amap.utils.MapUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 爬取高德数据服务
 *
 * @author zhangjun
 * @date 2018/12/3
 */
@Service
public class SpiderMapService {

    @Resource
    private MapConfiguration mapConfiguration;
    @Resource
    private EmailService emailService;


    /**
     * 开始爬取
     */
    public void startSpider() {
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        initMapSize();
    }

    /**
     * 初始化地图分块大小
     */
    private void initMapSize() {

        int distance = mapConfiguration.getDistance();
        double longitudeDistance = MapConfiguration.ONE_LONGITUDE * distance;
        double latitudeDistance = MapConfiguration.ONE_LATITUDE * distance;
        double[] northeast = null;
        double[] southwest = null;
        try {
            northeast = MapUtils.splitString(mapConfiguration.getNortheast());
            southwest = MapUtils.splitString(mapConfiguration.getSouthwest());
        } catch (Exception e) {
            emailService.send(EmailTypeEnum.START_ERROR, "东北角或西南角经纬度填写错误");
            throw new RuntimeException("东北角或西南角经纬度填写错误");
        }

        int longitudeSize = 0;
        if ((northeast[0] - southwest[0]) % longitudeDistance != 0) {
            longitudeSize = (int) ((northeast[0] - southwest[0]) / longitudeDistance + 1);
        } else {
            longitudeSize = (int) ((northeast[0] - southwest[0]) / longitudeDistance);
        }

        int latitudeSize = 0;
        if ((northeast[1] - southwest[1]) % latitudeDistance != 1) {
            latitudeSize = (int) ((northeast[1] - southwest[1]) / latitudeDistance + 1);
        } else {
            latitudeSize = (int) ((northeast[1] - southwest[1]) / latitudeDistance);
        }
        computeSize(northeast, longitudeDistance, longitudeSize, latitudeDistance, latitudeSize);
    }

    /**
     * 计算每个块的经纬度
     *
     * @param northeast
     * @param longitudeDistance
     * @param longitudeSize
     * @param latitudeDistance
     * @param latitudeSize
     */
    private void computeSize(double[] northeast, double longitudeDistance, int longitudeSize, double latitudeDistance, int latitudeSize) {

    }


}
