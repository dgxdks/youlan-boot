package com.youlan.common.core.helper;

import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;

public class FileHelper {
    /**
     * 通过文件名称获取媒体类型
     */
    public static MediaType getMediaTypeByFileName(String fileName) {
        return MediaTypeFactory.getMediaType(fileName).orElse(MediaType.APPLICATION_OCTET_STREAM);
    }

    /**
     * 通过文件名称获取ContentType
     */
    public static String getContentTypeByFileName(String fileName) {
        return getMediaTypeByFileName(fileName).toString();
    }
}
