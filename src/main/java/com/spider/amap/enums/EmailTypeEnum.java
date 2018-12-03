package com.spider.amap.enums;

/**
 * 邮件发送类型
 *
 * @author zhangjun
 * @date 2018/12/3
 */
public enum EmailTypeEnum {

    START_ERROR("启动错误", 1);

    private String name;
    private int code;

    EmailTypeEnum(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }
}
