package com.youlan.common.core.helper;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.web.multipart.MultipartFile;

public class FileHelper {
    public static final String[] IMAGE_EXT_NAMES = {"bmp", "gif", "jpg", "jpeg", "png"};

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

    /**
     * 通过文件上传对象获取媒体类型
     */
    public static MediaType getMediaTypeByMultipartFile(MultipartFile multipartFile) {
        return getMediaTypeByFileName(multipartFile.getOriginalFilename());
    }

    /**
     * 是否为图片媒体类型
     */
    public static boolean isImageExtName(MultipartFile multipartFile) {
        String fileName = multipartFile.getOriginalFilename();
        if (isImageExtName(fileName)) {
            return true;
        }
        String contentType = multipartFile.getContentType();
        return StrUtil.containsAnyIgnoreCase(contentType, "image");
    }

    /**
     * 是否是图片文件扩展名
     */
    public static boolean isImageExtName(String fileName) {
        return StrUtil.equalsAnyIgnoreCase(getExtName(fileName), IMAGE_EXT_NAMES);
    }

    /**
     * 获取文件拓展名
     */
    public static String getExtName(String fileName) {
        return FileNameUtil.extName(fileName);
    }
}
