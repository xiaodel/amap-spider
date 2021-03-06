package com.spider.amap.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 地图配置信息
 *
 * @author zhangjun
 * @date 2018/10/16
 */

@Getter
@Setter
@ToString
@Configuration
@ConfigurationProperties(prefix = "spider.city.map")
public class MapConfiguration {


    public final static double ONE_LATITUDE = 0.00000899;//1米的纬度

    public final static double ONE_LONGITUDE = 0.00001141; //1米的经度

    private List<String> key;//高德key

    private String northeast; //东北角经纬度

    private String southwest; //西南角经纬度

    private String keywords;  //查询关键词

    private String types;//查询POI类型

    private int distance = 4000;  //搜索、分片距离(默认4KM)

}
