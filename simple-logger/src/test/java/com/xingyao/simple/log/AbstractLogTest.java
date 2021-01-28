package com.xingyao.simple.log;

import org.junit.Test;

/**
 * @Author ranger
 * @Date 2021/1/26 22:57
 **/
public abstract class AbstractLogTest {

    public abstract Log getLog();

    @Test
    public void testLog(){
        Log log = this.getLog();

        log.info("log info message");

        log.debug("log debug message");

        System.out.println(log.isDebugEnabled());


        log.error("log error msg",new RuntimeException("test exception"));

        log.error("log error, test for error level log message,",new IllegalStateException("test"));



    }
}
