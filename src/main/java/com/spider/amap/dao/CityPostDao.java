package com.spider.amap.dao;

import com.spider.amap.model.CityPost;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author zhangjun
 * @date 2018/12/12
 */
public interface CityPostDao extends CrudRepository<CityPost, Long> {

    List<CityPost> getByLevel(int level);

    List<CityPost> getByParentId(long parentId);

}
