package org.tafia.spider.component;

import java.util.function.Consumer;

/**
 * Created by Dason on 2017/4/8.
 */
@FunctionalInterface
public interface DataConsumer {

    void comsume(Consumer<DataMessage> consumer);
}
