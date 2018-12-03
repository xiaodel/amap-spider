package com.spider.amap.service;

import com.spider.amap.config.EmailConfiguration;
import com.spider.amap.enums.EmailTypeEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhangjun
 * @date 2018/12/3
 */
@Service
public class EmailService {

    private final static Logger logger = LogManager.getLogger(EmailService.class);
    @Resource
    private EmailSenderService emailSenderService;
    @Resource
    private EmailConfiguration emailConfiguration;

    /**
     * 发送邮件
     *
     * @param typeEnum
     * @param message
     */
    public void send(EmailTypeEnum typeEnum, String message) {

        List<String> emailList = emailConfiguration.getAdmin();

        try {
            emailSenderService.sendText(typeEnum.getName(), message, emailList.toArray(new String[emailList.size()]));
        } catch (Exception e) {
            logger.error("邮件发送失败:" + typeEnum + ",消息内容:" + message, e);
        }
    }
}
