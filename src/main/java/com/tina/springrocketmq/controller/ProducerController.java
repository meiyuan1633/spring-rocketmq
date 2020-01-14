package com.tina.springrocketmq.controller;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/con")
@RestController
public class ProducerController {


    @Resource
    private DefaultMQProducer defaultMQProducer;

    @Value("${mq.producer.topic}")
    private String topic;

    @Value("${mq.producer.tag}")
    private String tag;

    @RequestMapping
    public Object product(String message) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        List<Message>messageList = new ArrayList<>();

        for (int i = 0; i <10 ; i++) {
            messageList.add(new Message(topic, tag, ("这是第" + i + "条消息").getBytes()));
        }
        /*生产信息*/
        defaultMQProducer.send(messageList);
        return "发送成功";
    }

}
