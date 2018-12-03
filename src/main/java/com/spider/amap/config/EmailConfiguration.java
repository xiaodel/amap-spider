package com.spider.amap.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 邮件配置信息
 *
 * @author zhangjun
 * @date 2018/10/16
 */

@Getter
@Setter
@ToString
@Configuration
@ConfigurationProperties(prefix = "spider.mail.system")
public class EmailConfiguration {

    private String sender;

    private List<String> admin;

}
