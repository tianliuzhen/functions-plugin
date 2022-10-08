package com.aaa.coreservice;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * 通过 URLClassLoader 加载外部jar，读取指定类
 *
 * https://www.cnblogs.com/changxy-codest/p/15846157.html
 * https://blog.csdn.net/fengxianaa/article/details/124450445
 *
 * @author liuzhen.tian
 * @version 1.0 TestUrlClassLoader.java  2022/7/21 20:39
 */
public class TestUrlClassLoader2 {
    public static void main(String[] args) throws Exception {

        // /加载插件包  读取位置： resources/plugin/plugin-provider.jar
        // ClassPathResource resource = new ClassPathResource("plugin/plugin-provider.jar");
        // 打印插件包路径
        // System.out.println(resource.getURL().getPath());

        // 打印当前类加载器
        System.out.println("Boot: " + TestUrlClassLoader2.class.getClass().getClassLoader());
        // 获取StringUtils的类全路径
        System.out.println("Boot: " + StringUtils.class.getResource("").getPath());
        // 模拟调用插件包

        /**
         * 这个是为了，测试 URLClassLoader 加载的jar，都是从指定源读取的。
         * 如下：不导入：jackson-core-2.11.0.jar、plugin-common-service-0.0.1-SNAPSHOT.jar
         */
        testSuccess();
    }

    public static void testSuccess() throws Exception {
        // 创建一个URL数组
        File file = new File("F:\\WorkSpace\\MyGithub\\functions\\plugin-biz\\target\\plugin-biz-0.0.1-SNAPSHOT.jar");
        URL[] urls = new URL[]{file.toURI().toURL()};

        URLClassLoader myClassLoader = new URLClassLoader(urls, null);

        // URLClassLoader myClassLoader = new PluginClassLoader(urls);

        // ClassLoader originClassLoader = Thread.currentThread().getContextClassLoader();
        /**
         这里需要临时更改当前线程的 ContextClassLoader
         避免中间件代码中存在Thread.currentThread().getContextClassLoader()获取类加载器
         因为它们会获取当前线程的 ClassLoader 来加载 class，而当前线程的ClassLoader极可能是App ClassLoader而非自定义的ClassLoader,
         也许是为了安全起见，但是这会导致它可能加载到启动项目中的class（如果有），或者发生其它的异常，所以我们在执行时需要临时的将当前线程的ClassLoader设置为自定义的ClassLoader，以实现绝对的隔离执行
         */
        // Thread.currentThread().setContextClassLoader(myClassLoader);


        Class<?> aClass = myClassLoader.loadClass("com.aaa.plugincommonservice.api.CommonUrlParse");
        Object obj = aClass.newInstance();

        // 利用反射创建对象
        Method method = aClass.getMethod("parse");

        //获取parse方法
        Object xxx = method.invoke(obj);

        System.out.println(xxx);
        // Thread.currentThread().setContextClassLoader(originClassLoader);
    }

}
