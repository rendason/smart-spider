package org.tafia.spider.component;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by Dason on 2017/4/2.
 */
@Component
@Lazy(false)
public class TaskMessageProcessorRegister implements ApplicationContextAware{

    @Autowired
    private TaskConsumer taskConsumer;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, TaskMessageProcessor> map = applicationContext.getBeansOfType(TaskMessageProcessor.class);
        map.values().forEach(this::register);
    }

    private void register(TaskMessageProcessor taskMessageProcessor) {
        taskConsumer.consume(taskMessageProcessor::process);
    }
}
