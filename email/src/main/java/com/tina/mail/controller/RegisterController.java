package com.tina.mail.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tina.mail.pojo.RegisterUser;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/reg")
public class RegisterController {

    @Value("${mq.producer.emailProducerGroup}")
    private String emailGroup;

    @Resource
    private ObjectMapper objectMapper;
    @Resource(name = "emailMQProducer")
    private DefaultMQProducer emailMQProducer;

    @Value("${mq.producer.emilTopic}")
    private String emailTopic;

    @Value("${mq.producer.emailTag}")
    private String emailTag;


    @RequestMapping
    public Object register(String email,String username) throws JsonProcessingException, InterruptedException, RemotingException, MQClientException, MQBrokerException {
        RegisterUser registerUser = new RegisterUser();
        registerUser.setEmail(email);
        registerUser.setUsername(username);
        /*写入数据*/
        Message message = new Message(this.emailTopic, this.emailTag,objectMapper.writeValueAsBytes(registerUser));
        /*发送数据*/
        emailMQProducer.send(message);
        return "邮件已发送，请注意查收";
    }

}
