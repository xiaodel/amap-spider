package com.spider.amap.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * 地图工具类
 *
 * @author zhangjun
 * @date 2018/12/3
 */
public class MapUtils {

    public static double[] splitString(String mapString) {
        if (StringUtils.isNotBlank(mapString)) {
            String[] split = StringUtils.split(mapString, ",");
            int length = split.length;
            double[] splitDouble = new double[length];
            for (int i = 0; i < length; i++) {
                try {
                    splitDouble[i] = Double.valueOf(split[i]);
                } catch (Exception e) {
                    throw new RuntimeException("字符串转换double失败");
                }
            }
            return splitDouble;
        }
        return null;
    }
}
