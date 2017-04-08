package org.tafia.spider.component;

/**
 * Created by Dason on 2017/4/1.
 */
@FunctionalInterface
public interface DataProducer {

    void produce(DataMessage message);

}
