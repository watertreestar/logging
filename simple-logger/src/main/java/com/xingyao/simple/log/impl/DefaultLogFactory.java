package com.xingyao.simple.log.impl;

import com.xingyao.simple.log.Log;
import com.xingyao.simple.log.LogConfigurationException;
import com.xingyao.simple.log.LogFactory;


/**
 * @Author ranger
 * @Date 2021/2/2 16:33
 **/
public class DefaultLogFactory extends LogFactory {
    @Override
    public Log getInstance(Class clazz) throws LogConfigurationException {
        return new SimpleLog(clazz.getName());
    }

    @Override
    public Log getInstance(String name) throws LogConfigurationException {
        return new SimpleLog(name);
    }
}
