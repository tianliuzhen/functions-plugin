package com.aaa.coreservice;

import java.io.*;

/**
 * @author liuzhen.tian
 * @version 1.0 MyClassLoader.java  2022/8/28 9:46
 */
public class MyClassLoader extends ClassLoader{
    //默认ApplicationClassLoader为父类加载器
    public MyClassLoader(){
        super();
    }

    //加载类的路径
    private String path = "";

    //重写findClass，调用defineClass，将代表类的字节码数组转换为Class对象
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] dataByte = new byte[0];
        try {
            dataByte = ClassDataByByte(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this.defineClass(name, dataByte, 0, dataByte.length);
    }

    //读取Class文件作为二进制流放入byte数组, findClass内部需要加载字节码文件的byte数组
    private byte[] ClassDataByByte(String name) throws IOException {
        InputStream is = null;
        byte[] data = null;
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        name = name.replace(".", "/"); // 为了定位class文件的位置，将包名的.替换为/
        is = new FileInputStream(new File(path + name + ".class"));
        int c = 0;
        while (-1 != (c = is.read())) { //读取class文件，并写入byte数组输出流
            arrayOutputStream.write(c);
        }
        data = arrayOutputStream.toByteArray(); //将输出流中的字节码转换为byte数组
        is.close();
        arrayOutputStream.close();
        return data;
    }
}
