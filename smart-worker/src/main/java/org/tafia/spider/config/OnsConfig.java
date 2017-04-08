package org.tafia.spider.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tafia.spider.component.DataProducer;
import org.tafia.spider.component.TaskConsumer;
import org.tafia.spider.component.TaskMessage;

import java.util.Properties;

/**
 * Created by Dason on 2017/4/1.
 */
@Configuration
@EnableConfigurationProperties(OnsProperties.class)
public class OnsConfig {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OnsProperties onsProperties;

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public Producer producer() {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.ProducerId, onsProperties.getProducerId());
        properties.put(PropertyKeyConst.AccessKey, onsProperties.getAccessKey());
        properties.put(PropertyKeyConst.SecretKey, onsProperties.getSecretKey());
        properties.put(PropertyKeyConst.ONSAddr, onsProperties.getAddress());
        logger.debug("Create producer by properties : {}", properties);
        Producer producer = ONSFactory.createProducer(properties);
        return producer;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public Consumer consumer() {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.ConsumerId, onsProperties.getConsumerId());
        properties.put(PropertyKeyConst.AccessKey, onsProperties.getAccessKey());
        properties.put(PropertyKeyConst.SecretKey, onsProperties.getSecretKey());
        properties.put(PropertyKeyConst.ONSAddr, onsProperties.getAddress());
        logger.debug("Create consumer by properties : {}", properties);
        Consumer consumer = ONSFactory.createConsumer(properties);
        return consumer;
    }

    @Bean
    public DataProducer dataProducer(Producer producer) {
        return message -> {
            String payload = JSON.toJSONString(message);
            String tags = "DataSource";
            Message m = new Message(onsProperties.getDataTopic(), tags, payload.getBytes());
            logger.debug("Send message : [topic={}, tags={}, body={}]",
                    onsProperties.getDataTopic(), tags, payload);
            producer.sendOneway(m);
        };
    }

    @Bean
    public TaskConsumer taskConsumer(Consumer consumer) {
        return taskConsumer -> {
            consumer.subscribe(onsProperties.getTaskTopic(), "*",
                    ((message, consumeContext) -> {
                String payload = new String(message.getBody());
                logger.debug("Receive message : [topic={}, body={}]",
                        onsProperties.getTaskTopic(), payload);
                JSONObject jsonObject = JSON.parseObject(payload);
                TaskMessage taskMessage = jsonObject.toJavaObject(TaskMessage.class);
                taskConsumer.accept(taskMessage);
                return Action.CommitMessage;
            }));
        };
    }

}
