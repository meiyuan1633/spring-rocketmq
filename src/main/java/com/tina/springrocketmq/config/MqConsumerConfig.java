package com.tina.springrocketmq.config;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.nio.charset.Charset;
import java.util.List;

@Configuration
@PropertySource("classpath:mq.properties")
public class MqConsumerConfig {

    @Value("${consumer.nameServer}")
    private String nameServer;
    @Value("myConsumerGroup")
    private String consumerGroup;

    @Value("myTopic")
    private String topic;

    /*消息的重复消费问题*/
    @Bean
    public DefaultMQPushConsumer defaultMQPushConsumer() throws MQClientException {
        DefaultMQPushConsumer defaultMQPushConsumer=new DefaultMQPushConsumer();
        defaultMQPushConsumer.setNamesrvAddr(this.nameServer);
        defaultMQPushConsumer.setConsumerGroup(this.consumerGroup);

        /*设置消费从哪个位置开始消费，重上一次消费结束的地方开始*/
        defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);

        /*第二个参数：subscription expression.it only support or operation such as "tag1 || tag2 || tag3" <br> if*/
        defaultMQPushConsumer.subscribe(this.topic,"tag1");

        /*注册消息的监听器*/
        defaultMQPushConsumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                System.out.println(Thread.currentThread().getName());
                msgs.forEach(me ->{
                    /*Charset.defaultCharset()默认编码格式*/
                    String message=new String(me.getBody(), Charset.defaultCharset());
                    System.out.println(message);
                });
                /**
                 * ConsumeConcurrentlyStatus.CONSUME_SUCCESS 跟mq确认消息已经消费了，然后MQ将消息标记为已消费消息，从队列中移除。
                 *
                 */

                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        } );
        defaultMQPushConsumer.start();
        return defaultMQPushConsumer;

    }
}
















