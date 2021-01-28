package com.xingyao.simple.log;

import com.xingyao.simple.log.impl.SimpleLog;


/**
 * @Author ranger
 * @Date 2021/1/26 22:49
 **/

public class SimpleLogTest extends AbstractLogTest {

    public Log getLog(){
        return new SimpleLog(this.getClass().getName());
    }

}
