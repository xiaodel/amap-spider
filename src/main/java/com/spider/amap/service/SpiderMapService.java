package com.spider.amap.service;

import com.spider.amap.config.MapConfiguration;
import com.spider.amap.enums.EmailTypeEnum;
import com.spider.amap.model.RegionModel;
import com.spider.amap.utils.MapUtils;
import com.spider.amap.utils.PropertiesUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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

    private final static String TEMPORARY_FILE_NAME = "temporary";

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
        double[] northeast, southwest;
        int longitudeSize, latitudeSize;
        try {
            northeast = MapUtils.splitString(mapConfiguration.getNortheast());
            southwest = MapUtils.splitString(mapConfiguration.getSouthwest());
        } catch (Exception e) {
            emailService.send(EmailTypeEnum.START_ERROR, "东北角或西南角经纬度填写错误");
            throw new RuntimeException("东北角或西南角经纬度填写错误");
        }

        if ((northeast[0] - southwest[0]) % longitudeDistance != 0) {
            longitudeSize = (int) ((northeast[0] - southwest[0]) / longitudeDistance + 1);
        } else {
            longitudeSize = (int) ((northeast[0] - southwest[0]) / longitudeDistance);
        }

        if ((northeast[1] - southwest[1]) % latitudeDistance != 1) {
            latitudeSize = (int) ((northeast[1] - southwest[1]) / latitudeDistance + 1);
        } else {
            latitudeSize = (int) ((northeast[1] - southwest[1]) / latitudeDistance);
        }

        List<RegionModel> regionList = computeSize(northeast, longitudeDistance, longitudeSize, latitudeDistance, latitudeSize);
        regionList.stream().forEach(model -> PropertiesUtil.put(model));
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
    private List<RegionModel> computeSize(double[] northeast, double longitudeDistance, int longitudeSize, double latitudeDistance, int latitudeSize) {
        List<RegionModel> regionModelList = new ArrayList();
        double longitude = northeast[0], latitude = northeast[1];
        for (int i = 0; i < longitudeSize; i++) {
            longitude -= (i * longitudeDistance);
            for (int j = 0; j < latitudeSize; j++) {
                RegionModel region = new RegionModel();

                latitude -= (j * latitudeDistance);
                double northwest = longitude - ((i + 1) * longitudeDistance);
                double southwest = latitude - ((j + 1) * latitudeDistance);

                region.setNortheast(longitude + "," + latitude); //东北
                region.setNorthwest(northwest + "," + latitude);//西北

                region.setSouthwest(northwest + "," + southwest);//西南
                region.setSoutheast(longitude + "," + southwest);//东南

                regionModelList.add(region);
            }
        }
        return regionModelList;
    }


}
