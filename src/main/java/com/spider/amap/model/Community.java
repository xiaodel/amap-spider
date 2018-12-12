package com.spider.amap.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 小区
 *
 * @author zhangjun
 */
@Getter
@Setter
@Entity
@Table(name = "community_common")
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;//小区名字

    private Double longitude;//小区经度

    private Double latitude;//小区纬度

    private String address;//小区地址

    private Long postId;//行政区id

    private String postName;//行政区名称

    private Long cityId;//城市id

    private String traffic;//交通状况(简要)

    @Column(length = 3000)
    private String photos;//小区图片

    private Integer status;//小区状态(0:审核中,1:审核通过)

}