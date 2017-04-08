package org.tafia.spider.component;

/**
 * Created by Dason on 2017/4/8.
 */
@FunctionalInterface
public interface TaskProducer {

    void produce(TaskMessage taskMessage);
}
