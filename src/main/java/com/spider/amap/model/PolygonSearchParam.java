package com.spider.amap.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 多边形搜索请求参数
 *
 * @author zhangjun
 * @date 2018/12/4
 */
@Getter
@Setter
public class PolygonSearchParam {

    private String location;//中心点坐标(必填)

    private String keywords;//查询关键词

    private String types;//查询POI类型

    private Integer radius;//查询半径

    private int offset = 20;//每页记录数据

    private int page = 1;//当前页数

    private String extensions = "all";//返回结果控制（all/base）

}
