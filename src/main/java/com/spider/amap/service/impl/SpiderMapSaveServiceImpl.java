package com.spider.amap.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.spider.amap.dao.CityPostDao;
import com.spider.amap.dao.CommunityDao;
import com.spider.amap.model.CityPost;
import com.spider.amap.model.Community;
import com.spider.amap.service.SpiderMapSaveService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhangjun
 * @date 2018/12/4
 */
@Service
public class SpiderMapSaveServiceImpl implements SpiderMapSaveService {

    private static ConcurrentHashMap postMap;
    @Resource
    private CommunityDao communityDao;
    @Resource
    private CityPostDao cityPostDao;

    private final static Logger logger = LogManager.getLogger(SpiderMapSaveServiceImpl.class);

    @Override
    public void saveSpiderData(JSONArray array) {
        if (MapUtils.isEmpty(postMap)) {
            initPostMap();
        }
        if (array != null && array.size() > 0) {
            array.stream().forEach(map -> {
                if (map == null) {
                    return;
                }
                JSONObject jsonObject = (JSONObject) map;
                String adcode = jsonObject.getString("adcode");
                if (postMap.containsKey(adcode)) {
                    CityPost post = (CityPost) postMap.get(adcode);
                    Community community = new Community();
                    community.setName(jsonObject.getString("name"));
                    String[] location = StringUtils.split(jsonObject.getString("location"), ",");
                    community.setLongitude(Double.valueOf(location[0]));
                    community.setLatitude(Double.valueOf(location[1]));
                    community.setAddress(jsonObject.getString("address"));
                    community.setPostId(post.getId());
                    community.setPostName(post.getName());
                    community.setCityId(post.getParentId());
                    JSONArray photos = jsonObject.getJSONArray("photos");
                    if (photos != null && photos.size() > 0) {
                        StringBuilder photosBuilder = new StringBuilder();
                        photos.stream().forEach(p -> photosBuilder.append(((JSONObject) p).getString("url")).append(","));
                        StringUtils.substringBeforeLast(photosBuilder.toString(), ",");
                        community.setPhotos(photosBuilder.toString());
                    }
                    Community communityInfo = communityDao.getByLongitudeAndLatitude(community.getLongitude(), community.getLatitude());
                    if (communityInfo == null) {
                        communityDao.save(community);
                    }
                }
            });
        }
    }

    /**
     * 初始化城市数据
     */
    private synchronized void initPostMap() {

        postMap = new ConcurrentHashMap();

        try {
            List<CityPost> cityPostList = cityPostDao.getByLevel(1);
            cityPostList.stream().forEach(cityPost -> cityPostDao.getByParentId(cityPost.getId()).stream().forEach(post -> postMap.put(post.getCode(), post)));
        } catch (Exception e) {
            logger.error("初始化城市信息失败");
        }
    }
}
