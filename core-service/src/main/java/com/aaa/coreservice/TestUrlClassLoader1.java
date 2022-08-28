package com.aaa.coreservice;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * 通过 URLClassLoader 加载外部jar，读取指定类
 * <p>
 * https://www.cnblogs.com/changxy-codest/p/15846157.html
 * https://blog.csdn.net/fengxianaa/article/details/124450445
 *
 * @author liuzhen.tian
 * @version 1.0 TestUrlClassLoader.java  2022/7/21 20:39
 */
public class TestUrlClassLoader1 {
    public static void main(String[] args) throws Exception {

        /**
         * 理解一下： classloader 加载类用的是全盘负责、委托机制
         * 所谓全盘负责，即是当一个classloader加载一个Class的时候，
         * 这个Class所依赖的和引用的所有 Class也由这个classloader负责载入，除非是显式的使用另外一个classloader载入；
         *
         * 这里测试一下：plugin-biz 的 CommonUrlParse类 执行parse需要类 jackson-core
         * 如果当前类加载器，不包含此 jackson-core ，则无法允许
         */
        test();
    }


    public static void test() throws Exception {
        // 这个 jar 是插件的jar
        File file = new File("F:\\WorkSpace\\MyGithub\\functions\\plugin-biz\\target\\plugin-biz-0.0.1-SNAPSHOT.jar");
        // 这个 jar 是跟插件冲突的jar版本位置
        File file2 = new File("F:\\MavenRepository\\com\\fasterxml\\jackson\\core\\jackson-core\\2.11.0\\jackson-core-2.11.0.jar");
        File file3 = new File("F:\\WorkSpace\\MyGithub\\functions\\plugin-common-service\\target\\plugin-common-service-0.0.1-SNAPSHOT.jar");

        URL[] urls = new URL[]{file.toURI().toURL(), file2.toURI().toURL(),file3.toURI().toURL()};

        /**
         * 这个此时是默认的类加载器机制（遵循双亲委派机制），
         * 当前类加载jackson-core 版本是：2.5.4，
         * 所以执行会报错：java.lang.NoSuchMethodError
         */
        URLClassLoader myClassLoader = new URLClassLoader(urls);
        Class<?> aClass = myClassLoader.loadClass("com.aaa.plugincommonservice.api.CommonUrlParse");
        Object obj = aClass.newInstance();
        // 利用反射创建对象
        Method method = aClass.getMethod("parse");
        //获取parse方法
        Object xxx = method.invoke(obj);

        System.out.println(xxx);
    }
}
