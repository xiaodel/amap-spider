package com.spider.amap.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 城市行政区
 *
 * @author zhangjun
 * @date 2018/11/20
 */
@Getter
@Setter
@Entity
@Table(name = "lezf_city_post")
public class CityPost {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;//城市id

    private String name;//名称

    private String code;//通用编码

    private Long parentId;//父id

    private Double longitude;//城市的经度

    private Double latitude;//城市纬度

    private Integer level;//1:城市,2:行政区

    private Integer status;//城市的开通状态

}
