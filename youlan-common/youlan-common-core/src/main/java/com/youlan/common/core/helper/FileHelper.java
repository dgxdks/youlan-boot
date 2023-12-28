package com.youlan.common.core.helper;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class FileHelper {
    public static final String[] IMAGE_EXT_NAMES = {"bmp", "gif", "jpg", "jpeg", "png"};

    /**
     * 通过文件名称获取媒体类型
     *
     * @param fileName 文件名称
     * @return 媒体类型
     */
    public static MediaType getMediaTypeByFileName(String fileName) {
        return MediaTypeFactory.getMediaType(fileName).orElse(MediaType.APPLICATION_OCTET_STREAM);
    }

    /**
     * 通过文件名称获取ContentType
     *
     * @param fileName 文件名称
     * @return ContentType
     */
    public static String getContentTypeByFileName(String fileName) {
        return getMediaTypeByFileName(fileName).toString();
    }

    /**
     * 通过文件上传对象获取媒体类型
     *
     * @param multipartFile 文件上传对象
     * @return 媒体类型
     */
    public static MediaType getMediaTypeByMultipartFile(MultipartFile multipartFile) {
        return getMediaTypeByFileName(multipartFile.getOriginalFilename());
    }

    /**
     * 是否为图片媒体类型
     *
     * @param multipartFile 文件上传对象
     * @return 是否为图片媒体类型
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
     *
     * @param fileName 文件名称
     * @return 是否是图片文件扩展名
     */
    public static boolean isImageExtName(String fileName) {
        return StrUtil.equalsAnyIgnoreCase(getExtName(fileName), IMAGE_EXT_NAMES);
    }

    /**
     * 获取文件拓展名
     *
     * @param fileName 文件名称
     * @return 文件拓展名
     */
    public static String getExtName(String fileName) {
        return FileNameUtil.extName(fileName);
    }

    /**
     * 创建临时文件
     *
     * @param content 文件内容
     * @return 临时文件
     */
    public static File createTempFile(String content) {
        File tempFile = createTempFile();
        FileUtil.writeString(content, tempFile, StandardCharsets.UTF_8);
        return tempFile;
    }

    /**
     * 创建临时文件
     *
     * @param content 文件内容
     * @return 临时文件
     */
    public static File createTempFile(byte[] content) {
        File tempFile = createTempFile();
        FileUtil.writeBytes(content, tempFile);
        return tempFile;
    }

    /**
     * 创建临时文件
     *
     * @return 临时文件
     */
    public static File createTempFile() {
        File tempFile = FileUtil.createTempFile(IdUtil.simpleUUID(), null, true);
        tempFile.deleteOnExit();
        return tempFile;
    }
}
