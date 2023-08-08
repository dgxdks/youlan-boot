package com.youlan.tools.utils;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Slf4j
public class VelocityUtil {

    /**
     * 初始化
     */
    public static void initVelocity() {
        Properties properties = new Properties();
        properties.setProperty("resource.loader.file.class" , "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        properties.setProperty(Velocity.INPUT_ENCODING, StandardCharsets.UTF_8.displayName());
        Velocity.init(properties);
    }

    /**
     * 生成指定模版内容
     */
    public static String mergeTemplate(String templateClassPath, VelocityContext velocityContext) {
        Template template = Velocity.getTemplate(templateClassPath);
        StringWriter stringWriter = new StringWriter();
        template.merge(velocityContext, stringWriter);
        return stringWriter.toString();
    }

    public static void writeTemplate(String homePath, String packageName, String fileName, String fileContent) {
        String packagePath = GeneratorUtil.packageName2Path(packageName);
        String writePath = homePath + File.separator + packagePath + File.separator + fileName;
        FileUtil.writeString(fileContent, writePath, StandardCharsets.UTF_8);
        log.info("写入模板文件: {homePath: {}, packageName: {}, fileName: {}}", homePath, packageName, fileName);
    }
}
