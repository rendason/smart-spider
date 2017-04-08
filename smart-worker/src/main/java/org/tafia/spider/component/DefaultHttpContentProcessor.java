package org.tafia.spider.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by Dason on 2017/4/8.
 */
@Component("default-http-content-processor")
public class DefaultHttpContentProcessor implements HttpContentProcessor {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public DataMessage process(HttpContent httpContent) {
        logger.info("raw text : \n{}", httpContent.getText());
        return null;
    }
}
