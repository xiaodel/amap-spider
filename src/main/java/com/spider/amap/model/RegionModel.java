package com.spider.amap.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 区域经纬度(某块区域)
 *
 * @author zhang jun
 * @date 2018/12/3
 */
@Getter
@Setter
@ToString
public class RegionModel {

    private String northeast;//东北

    private String northwest;//西北

    private String southwest;//西南

    private String southeast;//东南

}
