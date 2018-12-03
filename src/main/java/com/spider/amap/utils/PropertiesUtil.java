package com.spider.amap.utils;

import com.alibaba.fastjson.JSON;
import com.spider.amap.model.RegionModel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * 文件读取工具类
 *
 * @author zhang jun
 * @date 2018/12/3
 */
public class PropertiesUtil {

    private final static String TEMPORARY_FILE_NAME = "temporary.properties";

    private static Properties prop = null;

    static {
        try {
            prop = new Properties();
            prop.load(PropertiesUtil.class.getClassLoader().getResourceAsStream(TEMPORARY_FILE_NAME));
            prop.clear();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static RegionModel get() {
        for (Object value : prop.values()) {
            return JSON.parseObject((String) value, RegionModel.class);
        }
        return null;
    }

    public static void put(RegionModel region) {
        String string = JSON.toJSONString(region);
        prop.setProperty(region.hashCode() + "", string);
    }

    public static void delete(RegionModel region) {
        prop.remove(JSON.toJSONString(region));
    }

    public static void main(String[] args) {
        RegionModel regionModel = PropertiesUtil.get();
        System.out.println(regionModel);
    }
}
