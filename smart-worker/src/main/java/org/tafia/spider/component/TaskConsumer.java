package org.tafia.spider.component;

import java.util.function.Consumer;

/**
 * Created by Dason on 2017/4/1.
 */
@FunctionalInterface
public interface TaskConsumer {

    void consume(Consumer<TaskMessage> consumer);

}
