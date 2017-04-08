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
import org.tafia.spider.component.DataConsumer;
import org.tafia.spider.component.DataMessage;
import org.tafia.spider.component.TaskMessage;
import org.tafia.spider.component.TaskProducer;

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
    public TaskProducer taskProducer(Producer producer) {
        return message -> {
            String payload = JSON.toJSONString(message);
            String tags = "DataSource";
            Message m = new Message(onsProperties.getTaskTopic(), tags, payload.getBytes());
            logger.debug("Send message : [topic={}, tags={}, body={}]",
                    onsProperties.getTaskTopic(), tags, payload);
            producer.sendOneway(m);
        };
    }

    @Bean
    public DataConsumer dataConsumer(Consumer consumer) {
        return dataConsumer -> {
            consumer.subscribe(onsProperties.getDataTopic(), "*",
                    ((message, consumeContext) -> {
                        String payload = new String(message.getBody());
                        logger.debug("Receive message : [topic={}, body={}]",
                                onsProperties.getDataTopic(), payload);
                        JSONObject jsonObject = JSON.parseObject(payload);
                        DataMessage dataMessage = jsonObject.toJavaObject(DataMessage.class);
                        dataConsumer.accept(dataMessage);
                        return Action.CommitMessage;
                    }));
        };
    }

}
