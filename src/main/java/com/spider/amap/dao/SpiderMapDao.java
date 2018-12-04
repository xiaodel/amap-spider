package com.spider.amap.dao;

import com.spider.amap.model.RegionModel;
import org.springframework.data.repository.CrudRepository;

/**
 * @author zhangjun
 * @date 2018/12/4
 */
public interface SpiderMapDao extends CrudRepository<RegionModel, Long> {

}
