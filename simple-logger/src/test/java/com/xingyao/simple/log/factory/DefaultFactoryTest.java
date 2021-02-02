package com.xingyao.simple.log.factory;

import com.xingyao.simple.log.AbstractLogTest;
import com.xingyao.simple.log.Log;
import com.xingyao.simple.log.LogFactory;

/**
 * @Author ranger
 * @Date 2021/2/2 16:56
 **/
public class DefaultFactoryTest extends AbstractLogTest {
    @Override
    public Log getLog() {
        return LogFactory.getLog(this.getClass());
    }
}
