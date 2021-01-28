package com.xingyao.simple.log.classloader;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.Assert;

import java.io.*;
import java.util.Enumeration;
import java.util.Properties;

/**
 * @Author ranger
 * @Date 2021/1/28 0:07
 **/
public class LoadResourceTest extends TestCase {

    public void testReadFile() throws FileNotFoundException {
        File file = new File("src/test/resources/logging.properties");
        if(file.exists()){
            System.out.println("file exists");
        }
        InputStream in = new FileInputStream(file);
    }

    public void testLoadResource() throws IOException {
        ClassLoader sysClassLoader = ClassLoader.getSystemClassLoader();

        InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream("logging.properties");
        Assert.assertNotEquals(in.available(),0);

        Properties properties = new Properties();
        properties.load(in);

        Enumeration names = properties.propertyNames();
        while(names.hasMoreElements()){
            System.out.println(names.nextElement());
        }
    }
}
