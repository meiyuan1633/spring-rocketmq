package com.tina.emails.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tina.emails.pojo.RegisterUser;
import com.tina.emails.service.EmailService;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@PropertySource("classpath:mq.properties")
public class MessageConsumerConfig {
    private Logger logger = LoggerFactory.getLogger(MessageConsumerConfig.class);

    @Resource
    private ObjectMapper objectMapper;

    @Value("${consumer.consumerGroup}")
    private String consumerGroup;

    @Value("${consumer.nameServer}")
    private String nameServer;

    @Value("${consumer.emailTopic}")
    private String emailTopic;

    @Value("${consumer.emailTag}")
    private String emailTag;

    @Resource
    private EmailService emailService;

    @Bean
    public DefaultMQPushConsumer defaultMQPullConsumer() throws MQClientException {
        DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer();;
        defaultMQPushConsumer.setNamesrvAddr(this.nameServer);
        defaultMQPushConsumer.setConsumerGroup(this.consumerGroup);

        /*设置消费从哪个位置开始消费，从上一次消费结束的地方开始*/
        defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        defaultMQPushConsumer.subscribe(this.emailTopic,this.emailTag);

        /*注册消息的监听器*/
        defaultMQPushConsumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
               msgs.forEach(me ->{
                   byte[] body = me.getBody();
                   try {
                       RegisterUser registerUser = objectMapper.readValue(body, 0, body.length, RegisterUser.class);
                       logger.info("发送邮件给"+registerUser.getEmail());
                       Map<String,Object>param=new HashMap<>();
                       param.put("name",registerUser.getUsername());
                       param.put("validataCode","9876");
                       emailService.sendMail(registerUser.getEmail(),"注册成功通知",param);
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               });

               /*
               * ConsumeConcurrentlyStatus.CONSUME_SUCCESS跟mq确认消息已经消费了，然后MQ将消息标记为已消费消息，从队列中移除。
               * */
               return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

            }

        });
        defaultMQPushConsumer.start();
        return defaultMQPushConsumer;
    }

}
