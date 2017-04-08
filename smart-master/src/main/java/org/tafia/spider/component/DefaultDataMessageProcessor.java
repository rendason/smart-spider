package org.tafia.spider.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by Dason on 2017/4/8.
 */
@Component
public class DefaultDataMessageProcessor implements DataMessageProcessor {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void process(DataMessage dataMessage) {
        logger.info(dataMessage.getTasks().toString());
    }
}
