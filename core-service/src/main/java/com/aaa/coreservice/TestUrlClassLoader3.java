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
 * https://zhuanlan.zhihu.com/p/185612299
 *
 * @author liuzhen.tian
 * @version 1.0 TestUrlClassLoader.java  2022/7/21 20:39
 */
public class TestUrlClassLoader3 {
    public static void main(String[] args) throws Exception {

        /**
         * 如果 loader 中也引用了jackson，不过版本是：2.5.4，这时候再次运行：
         *      Caused by: java.lang.NoSuchMethodError: com.fasterxml.jackson.core.JsonFactory.getFormatGeneratorFeatures()I
         * 	    at com.aaa.plugincommonservice.api.CommonUrlParse.parse(CommonUrlParse.java:25)
         * 	    ... 6 more
         *
         * 报错，找不到：getFormatGeneratorFeatures() 方法
         *
         * 原因：myClassLoader.loadClass("com.test.ParseExcel")
         *      myClassLoader 的 parent 是 AppClassLoader
         *      根据双亲委派模式，myClassLoader 加载 JsonFactory 之前，会先让 AppClassLoader 去加载
         *      而 AppClassLoader 从自己的 classpath 找到了这个类，加载成功，不过版本是2.5.4
         *      但是2.5.4的版本中并没有getFormatGeneratorFeatures方法，所以。。。。
         *
         * 解决:
         *      设置父加载器为null或者为 ExtClassLoader
         *      为什么能解决呢？
         *          设置父加载器为null或者Ext，就打破了双亲委派，就当前加载器自己去加载，把向上传递机制给断了
         *      这个是能解决：jackson-core 版本 冲突的问题
         *
         * 加载过程：
         *  父加载器 = ExtClassLoader:
         *      myClassLoader 的父亲是 ExtClassLoader，所以这时候 myClassLoader 跟 appClassLoader 是兄弟关系
         *      myClassLoader 在加载 CommonUrlParse 时，会加载 JsonFactory，同时遵循双亲委派模式
         *      所以 ExtClassLoader 会尝试加载 JsonFactory 这个类，不会加载成功，转而由 myClassLoader 自己去加载 JsonFactory，加载成功，版本是2.11
         *      2.11 版本的 JsonFactory 有 getFormatGeneratorFeatures 方法，所以正常运行
         *
         *  父加载器 =  null
         *      这个就比较好理解了，父类为空，只能自己去加载，自己去加载就打破双亲委派机制了，
         *      只在自己的内部容器中寻找类去加载
         *
         * 缺陷：
         *      如果这样解决，所有的引用jar都要从 设置的URLClassLoader 进行解析了
         *      如下面代码：plugin-common-service 也要设置，否则无法解析
         */

        // 其实要尽量避免这种jar版本冲突的问题，而不是从加载的方式去解决

        // 打印当前类加载器
        System.out.println("当前类加载器: " + TestUrlClassLoader3.class.getClassLoader());
        // 打印当前类加载器Parent
        System.out.println("当前类加载器父类: " + TestUrlClassLoader3.class.getClassLoader().getParent());

        test();
    }

    public static void test() throws Exception {
        // 这个 jar 是插件的jar
        File file = new File("F:\\WorkSpace\\MyGithub\\functions\\plugin-biz\\target\\plugin-biz-0.0.1-SNAPSHOT.jar");
        // 这个 jar 是跟插件冲突的jar版本位置
        File file2 = new File("F:\\MavenRepository\\com\\fasterxml\\jackson\\core\\jackson-core\\2.11.0\\jackson-core-2.11.0.jar");
        File file3 = new File("F:\\WorkSpace\\MyGithub\\functions\\plugin-common-service\\target\\plugin-common-service-0.0.1-SNAPSHOT.jar");
        URL[] urls = new URL[]{file.toURI().toURL(), file2.toURI().toURL(),file3.toURL()};

        ClassLoader extClassLoader = TestUrlClassLoader3.class.getClassLoader().getParent();
        URLClassLoader myClassLoader = new URLClassLoader(urls, extClassLoader);

        // 设置父加载器为null跟 extClassLoader 加载效果是一样的，都能读取jackson-core-2.11.0.jar
        // URLClassLoader myClassLoader = new URLClassLoader(urls, null);

        Class<?> aClass = myClassLoader.loadClass("com.aaa.plugincommonservice.api.CommonUrlParse");
        Object obj = aClass.newInstance();
        // 利用反射创建对象
        Method method = aClass.getMethod("parse");
        //获取parse方法
        Object xxx = method.invoke(obj);

        System.out.println(xxx);

    }

}
