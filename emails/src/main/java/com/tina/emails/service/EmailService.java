package com.tina.emails.service;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;

/*
* 如果回滚异常，要有重试机制。重试3次，如果重试失败，记录数据，预警系统给对应的负责人发送短信和邮件，人工干预。
* */
@Service
public class EmailService {

    @Resource
    private TemplateEngine templateEngine;

    @Resource
    private JavaMailSender javaMailSender;

    /*
    *
    * */
    public void sendMail(String to, String subject, Map<String,Object>map){
        MimeMessage mimeMessage=javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper=null;

        try {
            mimeMessageHelper=new MimeMessageHelper(mimeMessage,true);
            //发送给哪个邮件
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);

            Context context = new Context();
            context.setVariables(map);
            String email = templateEngine.process("email", context);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (MailException ex){
            ex.printStackTrace();
        }
        /*
        * 运行时异常：继承了RuntimeException，而没有继承Exception都是运行时异常
        * 非运行时异常：继承了Exception，而没有继承继承了RuntimeException都是非运行时异常
        *
        *
        * */
    }


}
