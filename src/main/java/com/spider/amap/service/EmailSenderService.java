package com.spider.amap.service;

import com.spider.amap.config.EmailConfiguration;
import com.spider.amap.utils.HttpUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 * 邮件服务
 *
 * @author zhangjun
 * @date 2018/11/30
 */
@Component
public class EmailSenderService {

    private final static Logger logger = LogManager.getLogger(EmailSenderService.class);
    @Resource
    private JavaMailSender javaMailSender;
    @Resource
    private EmailConfiguration emailConfiguration;

    /**
     * 发送文本邮件
     *
     * @param title
     * @param content
     * @param emailAddress
     * @return
     */
    public boolean sendText(String title, String content, String... emailAddress) {

        return send(title, content, false, null, null, emailAddress);
    }

    /**
     * 发送文本及附件
     *
     * @param title
     * @param content
     * @param fileName
     * @param filePath
     * @param emailAddress
     * @return
     */
    public boolean sendTextAndAttachment(String title, String content, String fileName, String filePath, String... emailAddress) {

        return send(title, content, false, fileName, new File(filePath), emailAddress);
    }

    /**
     * 发送html文本内容
     *
     * @param title
     * @param htmlText
     * @param emailAddress
     * @return
     */
    public boolean sendHtmlText(String title, String htmlText, String... emailAddress) {

        return send(title, htmlText, true, null, null, emailAddress);
    }

    /**
     * 发送html文本内容及附件
     *
     * @param title
     * @param htmlText
     * @param fileName
     * @param filePath
     * @param emailAddress
     * @return
     */
    public boolean sendHtmlTextAndAttachment(String title, String htmlText, String fileName, String filePath, String... emailAddress) {

        return send(title, htmlText, true, fileName, new File(filePath), emailAddress);

    }

    /**
     * 发送html链接地址
     *
     * @param title
     * @param htmlAddress
     * @param emailAddress
     * @return
     */
    public boolean sendHtmlAddress(String title, String htmlAddress, String... emailAddress) {

        return send(title, HttpUtils.get(htmlAddress), true, null, null, emailAddress);
    }

    /**
     * 发送html链接地址及附件
     *
     * @param title
     * @param htmlAddress
     * @param fileName
     * @param filePath
     * @param emailAddress
     * @return
     */
    public boolean sendHtmlAddressAndAttachment(String title, String htmlAddress, String fileName, String filePath, String... emailAddress) {

        return send(title, HttpUtils.get(htmlAddress), true, fileName, new File(filePath), emailAddress);
    }

    /**
     * @param title        邮件标题
     * @param content      邮件内容(或地址)
     * @param isHtml       是否时html
     * @param fileName     附件名称
     * @param file         附件文件流
     * @param emailAddress 接收者地址
     * @return
     */
    private boolean send(String title, String content, boolean isHtml, String fileName, File file, String... emailAddress) {

        if (emailAddress.length <= 0) {
            return true;
        }

        MimeMessage mailMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper message = null;

        try {

            if (StringUtils.isNotBlank(fileName) && file != null) {
                message = new MimeMessageHelper(mailMessage, true);
                message.addAttachment(fileName, file);
            } else {
                message = new MimeMessageHelper(mailMessage);
            }

            message.setFrom(emailConfiguration.getSender());
            message.setSubject(title);
            message.setTo(emailAddress);
            message.setText(content, isHtml);

            javaMailSender.send(mailMessage);
        } catch (Exception e) {
            logger.error("发送邮件失败,接收人:" + emailAddress + ",标题:" + title + ",内容:" + content + ",文件名:" + fileName, e);
        }
        return true;
    }
}
