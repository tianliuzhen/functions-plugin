package com.aaa.plugincommonservice.api;

import com.aaa.plugincommonservice.FunctionExecutorService;
import com.aaa.plugincommonservice.annotion.FunctionDefinition;
import com.aaa.plugincommonservice.util.DataCheck;
import com.fasterxml.jackson.core.JsonFactory;

/**
 * @author liuzhen.tian
 * @version 1.0 CommonUrlParse.java  2022/7/23 20:59
 */

public class CommonUrlParse2 {

    public String execute(String request) {
        return "execute: " + request;
    }

    public void parse() {
        DataCheck.isBoolean();

        System.out.println("执行解析Excel方法........");
        JsonFactory factory = new JsonFactory();
        System.out.println("执行jsonFaction的getFormatGeneratorFeatures方法：" + factory.getFormatGeneratorFeatures());
    }

}
