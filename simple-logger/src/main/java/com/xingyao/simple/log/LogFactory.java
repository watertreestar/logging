package com.xingyao.simple.log;

import com.xingyao.simple.log.impl.DefaultLogFactory;

/**
 * @Author ranger
 * @Date 2021/1/22 16:59
 * Factory for creating {@link Log} instances.
 **/
public abstract class LogFactory {

    public abstract Log getInstance(Class clazz) throws LogConfigurationException;

    public abstract Log getInstance(String name) throws LogConfigurationException;

    public static Log getLog(String name){
      return getFactory().getInstance(name);
    }

    public static Log getLog(Class clazz){
        return getFactory().getInstance(clazz);
    }

    /**
     * get LogFactory instance
    * */
    private static LogFactory getFactory(){
        return LogFactoryHolder.logFactory;
    }

    private static class LogFactoryHolder{
        static LogFactory logFactory = new DefaultLogFactory();
    }
}
