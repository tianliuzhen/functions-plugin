package com.aaa.coreservice;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author liuzhen.tian
 * @version 1.0 SimpleClassLoader.java  2022/7/23 21:56
 */
public class SimpleClassLoader extends URLClassLoader {

    // 中间件jar路径
    private static final String JAR_BASE_PATH = "e:/middleware/";

    public SimpleClassLoader(URL[] urls) {
        //parent 设置为null 则不会被 system classloader 加载
        super(urls, null);
        try {
            super.addURL(urls[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
//        if("com.qiyi.middleware.service.IMiddlewareService".equals(name)){
//            return SimpleClassLoader.class.getClassLoader().loadClass(name);
//        }
        try {
            return super.findClass(name);
        } catch (ClassNotFoundException e) {
            return SimpleClassLoader.class.getClassLoader().loadClass(name);
        }
    }
}

