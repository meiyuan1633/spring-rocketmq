package com.tina.mail.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tina.mail.pojo.User;
import com.tina.mail.service.UserService;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/con")
public class ProducerController {
    private static Logger logger= LoggerFactory.getLogger(ProducerController.class);
    @Resource
    private DefaultMQProducer defaultMQProducer;

    /*事务*/
    @Resource
    private TransactionMQProducer transactionMQProducer;

    @Resource
    private UserService userService;
    @Resource
    private ObjectMapper objectMapper;

    /*生产组名*/
    @Value("${mq.producer.producerGroup}")
    private String producerGroup;

    /*事务组*/
    @Value("${mq.producer.txProducerGroup}")
    private String txProducerGroup;

    @Value("${mq.producer.topic}")
    private String topic;

    @Value("${mq.producer.tag}")
    private String tag;

    @Value("${mq.producer.tx.topic}")
    private String txTopic;

    @Value("${mq.producer.tx.tag}")
    private String txTag;

    @RequestMapping
    public Object product(String message) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        List<Message> messageList=new ArrayList<>();
        for (int i = 0; i <10 ; i++) {
            messageList.add(new Message(topic,tag,("这是第"+i+"条消息").getBytes()));
        }
        defaultMQProducer.setProducerGroup(this.producerGroup);

        defaultMQProducer.send(messageList);
        return "发送成功";
    }


    /*事务*/
    @RequestMapping("/tx")
    public Object txMessage() throws JsonProcessingException, MQClientException {
        User user = new User();
        user.setId(1);
        user.setName("小明");
        user.setStamp(System.currentTimeMillis());

        /*写入*/
        Message message = new Message(txTopic, txTag, objectMapper.writeValueAsBytes(user));
        /*半消息，是被消息的消费者消费不到的消息，提交之后才能被消费*/
        transactionMQProducer.setProducerGroup(this.txProducerGroup);
        transactionMQProducer.sendMessageInTransaction(message,user);
        return "半消息投递成功";
    }

}
