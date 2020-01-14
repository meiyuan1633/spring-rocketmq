package com.tina.springrocketmq.config;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
* @ConfigurationProperties 该注解只能用在
*
* */
@Configuration
@ConfigurationProperties(prefix = "mq.producer")
public class MqProducerConfig {

    private String nameServer;
    private String producerGroup;

    public String getNameServer() {
        return nameServer;
    }

    public void setNameServer(String nameServer) {
        this.nameServer = nameServer;
    }

    public String getProducerGroup() {
        return producerGroup;
    }

    public void setProducerGroup(String producerGroup) {
        this.producerGroup = producerGroup;
    }

    @Bean
    public DefaultMQProducer defaultMQProducer(){
        // 实例消息的生产者，然后通过构造方法制定其组
        DefaultMQProducer defaultMQProducer=new DefaultMQProducer();
        defaultMQProducer.setNamesrvAddr(this.nameServer);
        defaultMQProducer.setProducerGroup(this.producerGroup);

        try {
            //启动producer
            defaultMQProducer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        return defaultMQProducer;
    }
}
