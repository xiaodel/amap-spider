package com.spider.amap.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.spider.amap.config.MapConfiguration;
import com.spider.amap.dao.SpiderMapDao;
import com.spider.amap.enums.EmailTypeEnum;
import com.spider.amap.exception.AMapException;
import com.spider.amap.model.OPSLimiter;
import com.spider.amap.model.PolygonSearchParam;
import com.spider.amap.model.RegionModel;
import com.spider.amap.utils.HttpUtils;
import com.spider.amap.utils.MapUtils;
import org.apache.catalina.util.URLEncoder;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
    @Resource
    private SpiderMapDao spiderMapDao;
    @Resource
    private SpiderMapSaveService spiderMapSaveService;

    private final static String POLYGON_URL = "https://restapi.amap.com/v3/place/polygon";//多边形搜索
    private final static Logger logger = LogManager.getLogger(SpiderMapService.class);
    //使用的第几个高德key
    private static int KEY_INDEX = 0;
    private AtomicInteger at;
    private OPSLimiter limiter;


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

        Iterable<RegionModel> regionModels = spiderMapDao.findAll();
        if (!regionModels.iterator().hasNext()) {
            initMapSize();
        }

        try {
            at = new AtomicInteger(0);
            limiter = new OPSLimiter(20);

            startSpiderData(regionModels);
        } catch (Exception e) {
            if (e instanceof AMapException) {
                throw new RuntimeException("自定义异常");
            } else {
                e.printStackTrace();
            }
        }
    }

    /**
     * 开始给爬去数据
     *
     * @param region
     */
    private void startSpiderData(Iterable<RegionModel> region) {
        Iterator<RegionModel> iterator = region.iterator();
        while (iterator.hasNext()) {
            RegionModel regionModel = iterator.next();
            getRegionModelPolygonData(regionModel, 1);
            spiderMapDao.delete(regionModel);
        }
    }

    private void getRegionModelPolygonData(RegionModel model, int pageIndex) {
        int pageNum = 20;

        PolygonSearchParam param = new PolygonSearchParam();
        String encode = URLEncoder.QUERY.encode("|", Charset.forName("utf-8"));
        param.setLocation(model.getNorthwest() + encode + model.getSoutheast());
        param.setKeywords(mapConfiguration.getKeywords());
        param.setTypes(mapConfiguration.getTypes());
        param.setRadius(mapConfiguration.getDistance());
        param.setOffset(pageNum);
        param.setPage(pageIndex);

        String polygonData = getPolygonData(param);

        if (StringUtils.isBlank(polygonData)) {
            return;
        }

        JSONObject parseObject = JSON.parseObject(polygonData);
        spiderMapSaveService.saveSpiderData(parseObject.getJSONArray("pois"));
        Integer count = parseObject.getInteger("count");
        if (count > pageIndex * pageNum) {
            getRegionModelPolygonData(model, 1 + pageIndex);
        }
    }

    private String getPolygonData(PolygonSearchParam param) {

        checkParam(param);

        Map<String, String> paramMap = new HashMap<>();

        paramMap.put("polygon", param.getLocation());
        paramMap.put("offset", String.valueOf(param.getOffset()));
        paramMap.put("page", String.valueOf(param.getPage()));

        if (StringUtils.isNotBlank(param.getKeywords())) {
            paramMap.put("keywords", param.getKeywords());
        }
        if (StringUtils.isNotBlank(param.getTypes())) {
            paramMap.put("types", param.getTypes());
        }
        if (param.getRadius() != null) {
            paramMap.put("radius", String.valueOf(param.getRadius()));
        }
        if (StringUtils.isNotBlank(param.getExtensions())) {
            paramMap.put("extensions", param.getExtensions());
        }

        return aMapHttpGet(POLYGON_URL, paramMap);
    }

    /**
     * 校验查询参数
     *
     * @param param
     */
    private void checkParam(PolygonSearchParam param) {
        if (param == null || StringUtils.isBlank(param.getLocation())) {
            throw new RuntimeException("查询参数错误");
        }
    }

    private String aMapHttpGet(String url, Map param) {

        String mapKey = mapConfiguration.getKey().get(KEY_INDEX);
        param.put("key", mapKey);

        try {

            String result = HttpUtils.get(url, param);

            JSONObject jsonObject = JSON.parseObject(result);

            if (jsonObject == null || StringUtils.isBlank(jsonObject.getString("infocode"))) {
                return null;
            }

            String code = jsonObject.getString("infocode");

            if ("10000".equals(code)) {
                return result;
            }

            if ("10001".equals(code)) {
                String errorMessage = "key:" + mapKey + ",不正确或过期。";
                emailService.send(EmailTypeEnum.START_ERROR, errorMessage);
                logger.error(errorMessage);
                return nextKey(url, param);
            }

            if ("10004".equals(code)) {
                return "开发者的单位时间内（1分钟）访问量超限";
            }

            if ("10010".equals(code)) {
                String errorMessage = "IP访问超限，明日继续！";
                emailService.send(EmailTypeEnum.START_ERROR, errorMessage);
                logger.error(errorMessage);
                throw new AMapException(errorMessage);
            }

            if ("10003".equals(code)) {
                String errorMessage = "key:" + mapKey + ",访问已超出日访问量";
                emailService.send(EmailTypeEnum.START_ERROR, errorMessage);
                logger.error(errorMessage);
                return nextKey(url, param);
            }

            if ("10009".equals(code)) {
                String errorMessage = "请求key:" + mapKey + ",与绑定平台不符";
                emailService.send(EmailTypeEnum.START_ERROR, errorMessage);
                logger.error(errorMessage);
                return nextKey(url, param);
            }

        } catch (Exception e) {
            logger.error("请求高德失败,url:" + url + ",请求参数:" + param, e);
        }
        return null;
    }

    /**
     * 获取下一个高德key
     */
    private String nextKey(String url, Map param) {
        KEY_INDEX = (mapConfiguration.getKey().size() - 1) <= KEY_INDEX ? 0 : KEY_INDEX + 1;
        if (KEY_INDEX == 0) {
            throw new AMapException("设置的key已经全部使用完，明日继续！");
        }
        return aMapHttpGet(url, param);
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

        try {
            spiderMapDao.saveAll(regionList);
        } catch (Exception e) {
            logger.error("初始化数据保存失败", e);
        }
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
        DecimalFormat format = new DecimalFormat("0.000000");
        for (int i = 0; i < longitudeSize; i++) {

            double nowLongitude = Double.valueOf(format.format(longitude - (Double.valueOf(format.format(i * longitudeDistance)))));
            for (int j = 0; j < latitudeSize; j++) {

                double nowLatitude = Double.valueOf(format.format(latitude - Double.valueOf(format.format((j * latitudeDistance)))));
                double northwest = Double.valueOf(format.format(longitude - (Double.valueOf(format.format((i + 1) * longitudeDistance)))));
                double southwest = Double.valueOf(format.format(latitude - Double.valueOf(format.format(((j + 1) * latitudeDistance)))));

                RegionModel region = new RegionModel();
                region.setNortheast(nowLongitude + "," + nowLatitude); //东北
                region.setNorthwest(northwest + "," + nowLatitude);//西北

                region.setSouthwest(northwest + "," + southwest);//西南
                region.setSoutheast(nowLongitude + "," + southwest);//东南

                regionModelList.add(region);
            }
        }
        return regionModelList;
    }


}
