package com.youlan.common.storage.proxy;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.xuyanwu.spring.file.storage.Downloader;
import cn.xuyanwu.spring.file.storage.FileInfo;
import cn.xuyanwu.spring.file.storage.FileStorageService;
import cn.xuyanwu.spring.file.storage.UploadPretreatment;
import cn.xuyanwu.spring.file.storage.aspect.FileStorageAspect;
import cn.xuyanwu.spring.file.storage.aspect.UploadAspectChain;
import cn.xuyanwu.spring.file.storage.exception.FileStorageRuntimeException;
import cn.xuyanwu.spring.file.storage.file.FileWrapper;
import cn.xuyanwu.spring.file.storage.file.FileWrapperAdapter;
import cn.xuyanwu.spring.file.storage.platform.FileStorage;
import cn.xuyanwu.spring.file.storage.recorder.FileRecorder;
import cn.xuyanwu.spring.file.storage.tika.ContentTypeDetect;
import cn.xuyanwu.spring.file.storage.util.Tools;

import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

/**
 * {@link FileStorageService}代理对象,用于返回指定的FileStorage
 */
public class FileStorageServiceProxy extends FileStorageService {
    private final FileStorageService fileStorageService;
    private final FileStorage fileStorage;

    public FileStorageServiceProxy(FileStorageService fileStorageService, FileStorage fileStorage) {
        this.fileStorageService = fileStorageService;
        this.fileStorage = fileStorage;
    }

    @Override
    public <T extends FileStorage> T getFileStorage() {
        return Tools.cast(fileStorage);
    }

    @Override
    public <T extends FileStorage> T getFileStorage(String platform) {
        return Tools.cast(fileStorage);
    }

    @Override
    public <T extends FileStorage> T getFileStorageVerify(FileInfo fileInfo) {
        return Tools.cast(fileStorage);
    }

    @Override
    public <T extends FileStorage> T getFileStorageVerify(String platform) {
        return Tools.cast(fileStorage);
    }

    @Override
    public FileInfo upload(UploadPretreatment pre) {
        FileWrapper file = pre.getFileWrapper();
        if (file == null) throw new FileStorageRuntimeException("文件不允许为 null ！");
        if (pre.getPlatform() == null) throw new FileStorageRuntimeException("platform 不允许为 null ！");

        FileInfo fileInfo = new FileInfo();
        fileInfo.setCreateTime(new Date());
        fileInfo.setSize(file.getSize());
        fileInfo.setOriginalFilename(file.getName());
        fileInfo.setExt(FileNameUtil.getSuffix(file.getName()));
        fileInfo.setObjectId(pre.getObjectId());
        fileInfo.setObjectType(pre.getObjectType());
        fileInfo.setPath(pre.getPath());
        fileInfo.setPlatform(pre.getPlatform());
        fileInfo.setAttr(pre.getAttr());
        fileInfo.setFileAcl(pre.getFileAcl());
        fileInfo.setThFileAcl(pre.getThFileAcl());
        if (StrUtil.isNotBlank(pre.getSaveFilename())) {
            fileInfo.setFilename(pre.getSaveFilename());
        } else {
            fileInfo.setFilename(IdUtil.objectId() + (StrUtil.isEmpty(fileInfo.getExt()) ? StrUtil.EMPTY : "." + fileInfo.getExt()));
        }
        fileInfo.setContentType(file.getContentType());

        byte[] thumbnailBytes = pre.getThumbnailBytes();
        if (thumbnailBytes != null) {
            fileInfo.setThSize((long) thumbnailBytes.length);
            if (StrUtil.isNotBlank(pre.getSaveThFilename())) {
                fileInfo.setThFilename(pre.getSaveThFilename() + pre.getThumbnailSuffix());
            } else {
                fileInfo.setThFilename(fileInfo.getFilename() + pre.getThumbnailSuffix());
            }
            if (StrUtil.isNotBlank(pre.getThContentType())) {
                fileInfo.setThContentType(pre.getThContentType());
            } else {
                fileInfo.setThContentType(fileStorageService.getContentTypeDetect().detect(thumbnailBytes, fileInfo.getThFilename()));
            }
        }

        FileStorage fileStorage = getFileStorage(pre.getPlatform());
        if (fileStorage == null) throw new FileStorageRuntimeException("没有找到对应的存储平台！");

        //处理切面
        return new UploadAspectChain(fileStorageService.getAspectList(), (_fileInfo, _pre, _fileStorage, _fileRecorder) -> {
            //真正开始保存
            if (_fileStorage.save(_fileInfo, _pre)) {
                if (_fileRecorder.save(_fileInfo)) {
                    return _fileInfo;
                }
            }
            return null;
        }).next(fileInfo, pre, fileStorage, fileStorageService.getFileRecorder());
    }

    @Override
    public FileInfo getFileInfoByUrl(String url) {
        return fileStorageService.getFileInfoByUrl(url);
    }

    @Override
    public boolean delete(String url) {
        return fileStorageService.delete(url);
    }

    @Override
    public boolean delete(String url, Predicate<FileInfo> predicate) {
        return fileStorageService.delete(url, predicate);
    }

    @Override
    public boolean delete(FileInfo fileInfo) {
        return fileStorageService.delete(fileInfo);
    }

    @Override
    public boolean delete(FileInfo fileInfo, Predicate<FileInfo> predicate) {
        return fileStorageService.delete(fileInfo, predicate);
    }

    @Override
    public boolean exists(String url) {
        return fileStorageService.exists(url);
    }

    @Override
    public boolean exists(FileInfo fileInfo) {
        return fileStorageService.exists(fileInfo);
    }

    @Override
    public Downloader download(FileInfo fileInfo) {
        return fileStorageService.download(fileInfo);
    }

    @Override
    public Downloader download(String url) {
        return fileStorageService.download(url);
    }

    @Override
    public Downloader downloadTh(FileInfo fileInfo) {
        return fileStorageService.downloadTh(fileInfo);
    }

    @Override
    public Downloader downloadTh(String url) {
        return fileStorageService.downloadTh(url);
    }

    @Override
    public boolean isSupportPresignedUrl(String platform) {
        return fileStorageService.isSupportPresignedUrl(platform);
    }

    @Override
    public boolean isSupportPresignedUrl(FileStorage fileStorage) {
        return fileStorageService.isSupportPresignedUrl(fileStorage);
    }

    @Override
    public String generatePresignedUrl(FileInfo fileInfo, Date expiration) {
        return fileStorageService.generatePresignedUrl(fileInfo, expiration);
    }

    @Override
    public String generateThPresignedUrl(FileInfo fileInfo, Date expiration) {
        return fileStorageService.generateThPresignedUrl(fileInfo, expiration);
    }

    @Override
    public boolean isSupportAcl(String platform) {
        return fileStorageService.isSupportAcl(platform);
    }

    @Override
    public boolean isSupportAcl(FileStorage fileStorage) {
        return fileStorageService.isSupportAcl(fileStorage);
    }

    @Override
    public boolean setFileAcl(FileInfo fileInfo, Object acl) {
        return fileStorageService.setFileAcl(fileInfo, acl);
    }

    @Override
    public boolean setThFileAcl(FileInfo fileInfo, Object acl) {
        return fileStorageService.setThFileAcl(fileInfo, acl);
    }

    @Override
    public UploadPretreatment of() {
        return fileStorageService.of();
    }

    @Override
    public UploadPretreatment of(Object source) {
        return fileStorageService.of(source);
    }

    @Override
    public UploadPretreatment of(Object source, String name) {
        return fileStorageService.of(source, name);
    }

    @Override
    public UploadPretreatment of(Object source, String name, String contentType) {
        return fileStorageService.of(source, name, contentType);
    }

    @Override
    public UploadPretreatment of(Object source, String name, String contentType, Long size) {
        return fileStorageService.of(source, name, contentType, size);
    }

    @Override
    public FileWrapper wrapper(Object source) {
        return fileStorageService.wrapper(source);
    }

    @Override
    public FileWrapper wrapper(Object source, String name) {
        return fileStorageService.wrapper(source, name);
    }

    @Override
    public FileWrapper wrapper(Object source, String name, String contentType) {
        return fileStorageService.wrapper(source, name, contentType);
    }

    @Override
    public FileWrapper wrapper(Object source, String name, String contentType, Long size) {
        return fileStorageService.wrapper(source, name, contentType, size);
    }

    @Override
    public <T> T invoke(String platform, String method, Object... args) {
        return fileStorageService.invoke(platform, method, args);
    }

    @Override
    public <T> T invoke(FileStorage platform, String method, Object... args) {
        return fileStorageService.invoke(platform, method, args);
    }

    @Override
    public void destroy() {
        fileStorageService.destroy();
    }

    @Override
    public FileStorageService getSelf() {
        return fileStorageService.getSelf();
    }

    @Override
    public FileRecorder getFileRecorder() {
        return fileStorageService.getFileRecorder();
    }

    @Override
    public CopyOnWriteArrayList<FileStorage> getFileStorageList() {
        return fileStorageService.getFileStorageList();
    }

    @Override
    public String getDefaultPlatform() {
        return fileStorageService.getDefaultPlatform();
    }

    @Override
    public String getThumbnailSuffix() {
        return fileStorageService.getThumbnailSuffix();
    }

    @Override
    public CopyOnWriteArrayList<FileStorageAspect> getAspectList() {
        return fileStorageService.getAspectList();
    }

    @Override
    public CopyOnWriteArrayList<FileWrapperAdapter> getFileWrapperAdapterList() {
        return fileStorageService.getFileWrapperAdapterList();
    }

    @Override
    public ContentTypeDetect getContentTypeDetect() {
        return fileStorageService.getContentTypeDetect();
    }

    @Override
    public void setSelf(FileStorageService self) {
        fileStorageService.setSelf(self);
    }

    @Override
    public void setFileRecorder(FileRecorder fileRecorder) {
        fileStorageService.setFileRecorder(fileRecorder);
    }

    @Override
    public void setFileStorageList(CopyOnWriteArrayList<FileStorage> fileStorageList) {
        fileStorageService.setFileStorageList(fileStorageList);
    }

    @Override
    public void setDefaultPlatform(String defaultPlatform) {
        fileStorageService.setDefaultPlatform(defaultPlatform);
    }

    @Override
    public void setThumbnailSuffix(String thumbnailSuffix) {
        fileStorageService.setThumbnailSuffix(thumbnailSuffix);
    }

    @Override
    public void setAspectList(CopyOnWriteArrayList<FileStorageAspect> aspectList) {
        fileStorageService.setAspectList(aspectList);
    }

    @Override
    public void setFileWrapperAdapterList(CopyOnWriteArrayList<FileWrapperAdapter> fileWrapperAdapterList) {
        fileStorageService.setFileWrapperAdapterList(fileWrapperAdapterList);
    }

    @Override
    public void setContentTypeDetect(ContentTypeDetect contentTypeDetect) {
        fileStorageService.setContentTypeDetect(contentTypeDetect);
    }
}
