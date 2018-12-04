package com.spider.amap.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 运行时所需要的临时表
 * 地图分块后的每块经纬度
 *
 * @author zhang jun
 * @date 2018/12/3
 */
@Getter
@Setter
@Entity
@Table(name = "temporary_map")
public class RegionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String northeast;//东北

    private String northwest;//西北

    private String southwest;//西南

    private String southeast;//东南

}
