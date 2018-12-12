package com.spider.amap.dao;

import com.spider.amap.model.Community;
import org.springframework.data.repository.CrudRepository;

/**
 * @author zhangjun
 * @date 2018/12/12
 */
public interface CommunityDao extends CrudRepository<Community, Long> {

    Community getByLongitudeAndLatitude(double longitude, double latitude);

}
