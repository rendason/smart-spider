package org.tafia.spider.component;

/**
 * Created by Dason on 2017/4/8.
 */
public interface HttpContentProcessor {

    DataMessage process(HttpContent httpContent);
}
