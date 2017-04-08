package org.tafia.spider.component;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by Dason on 2017/4/8.
 */
@Component
@Lazy(false)
public class DataMessageProcessorRegister implements ApplicationContextAware{

    @Autowired
    private DataConsumer dataConsumer;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, DataMessageProcessor> map = applicationContext.getBeansOfType(DataMessageProcessor.class);
        map.values().forEach(this::register);
    }

    private void register(DataMessageProcessor dataMessageProcessor) {
        dataConsumer.comsume(dataMessageProcessor::process);
    }
}
