package org.tafia.spider.util;

import java.util.UUID;

/**
 * Created by Dason on 2017/4/8.
 */
public class IdGen {

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
