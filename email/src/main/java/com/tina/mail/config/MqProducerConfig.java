package com.tina.mail.config;


import com.tina.mail.pojo.User;
import com.tina.mail.service.UserService;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * @ConfigurationProperties 该注解只能用在
 */
@Configuration
@ConfigurationProperties(prefix = "mq.producer")
public class MqProducerConfig {

    private static Logger logger = LoggerFactory.getLogger(MqProducerConfig.class);

    @Resource
    private UserService userService;

    private String nameServer;

    private String producerGroup;

    private String txProducerGroup;

    private String emailProducerGroup;

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

    public String getTxProducerGroup() {
        return txProducerGroup;
    }

    public void setTxProducerGroup(String txProducerGroup) {
        this.txProducerGroup = txProducerGroup;
    }

    public String getEmailProducerGroup() {
        return emailProducerGroup;
    }

    public void setEmailProducerGroup(String emailProducerGroup) {
        this.emailProducerGroup = emailProducerGroup;
    }

    @Bean("emailMQProducer")
    public DefaultMQProducer emailMQProducer() {
        DefaultMQProducer defaultMQProducer = new DefaultMQProducer();
        defaultMQProducer.setNamesrvAddr(this.nameServer);
        defaultMQProducer.setProducerGroup(this.emailProducerGroup);

        try {
            defaultMQProducer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        return defaultMQProducer;
    }

    @Bean
    public DefaultMQProducer defaultMQProducer() {
        DefaultMQProducer defaultMQProducer = new DefaultMQProducer();
        defaultMQProducer.setNamesrvAddr(this.nameServer);
        defaultMQProducer.setProducerGroup(this.producerGroup);

        try {
            defaultMQProducer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        return defaultMQProducer;
    }

    @Bean
    public TransactionMQProducer transactionMQProducer() {
        TransactionMQProducer transactionMQProducer = new TransactionMQProducer();
        transactionMQProducer.setNamesrvAddr(this.nameServer);
        transactionMQProducer.setProducerGroup(this.txProducerGroup);

        try {
            transactionMQProducer.start();
            transactionMQProducer.setTransactionListener(new TransactionListener(){

                // 执行本地事务
                @Override
                public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {

                    // insert into user values('xxx', 'vvv');   this.userId = userId;
                    //LocalTransactionState.ROLLBACK_MESSAGE 回滚半消息
                    //LocalTransactionState.COMMIT_MESSAGE 提交半消息
                    try{
                        userService.insertUser((User)arg);
                        System.out.println("本地事务执行成功");
                        try {
                            Thread.sleep(50000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return LocalTransactionState.COMMIT_MESSAGE;   // 事务执行成功
                    }catch (Exception ex) {
                        logger.error(ex.getMessage());
                        return LocalTransactionState.ROLLBACK_MESSAGE;
                    }
                }

                // 检查本地事务状态，当半消息在很久没有收到消息的确认或者回滚，会不停的询问本地事务，怎么弄？
                @Override
                public LocalTransactionState checkLocalTransaction(MessageExt msg) {
                    System.out.println(new String(msg.getBody(), Charset.defaultCharset()).intern());
                    System.out.println("半消息事务检查........");
                    return LocalTransactionState.COMMIT_MESSAGE;
                }
            });
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        return transactionMQProducer;
    }
}
