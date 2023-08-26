package org.mydrive.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

import org.mydrive.entity.enums.DateTimePatternEnum;
import org.mydrive.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;


public class FileShare implements Serializable {

    private String shareId;

    private String fileId;

    private String userId;

    private Integer validType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date shareTime;

    private String code;

    private Integer showCount;

    private String fileName;

    private String fileCover;

    private Integer fileCategory;

    private Integer fileType;

    private Integer folderType;

    public String getFileCover() {
        return fileCover;
    }

    public void setFileCover(String fileCover) {
        this.fileCover = fileCover;
    }

    public Integer getFileCategory() {
        return fileCategory;
    }

    public void setFileCategory(Integer fileCategory) {
        this.fileCategory = fileCategory;
    }

    public Integer getFileType() {
        return fileType;
    }

    public void setFileType(Integer fileType) {
        this.fileType = fileType;
    }

    public Integer getFolderType() {
        return folderType;
    }

    public void setFolderType(Integer folderType) {
        this.folderType = folderType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getShareId() {
        return this.shareId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileId() {
        return this.fileId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setValidType(Integer validType) {
        this.validType = validType;
    }

    public Integer getValidType() {
        return this.validType;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public Date getExpireTime() {
        return this.expireTime;
    }

    public void setShareTime(Date shareTime) {
        this.shareTime = shareTime;
    }

    public Date getShareTime() {
        return this.shareTime;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public void setShowCount(Integer showCount) {
        this.showCount = showCount;
    }

    public Integer getShowCount() {
        return this.showCount;
    }

    @Override
    public String toString() {
        return "FileShare{" +
                "shareId='" + shareId + '\'' +
                ", fileId='" + fileId + '\'' +
                ", userId='" + userId + '\'' +
                ", validType=" + validType +
                ", expireTime=" + expireTime +
                ", shareTime=" + shareTime +
                ", code='" + code + '\'' +
                ", showCount=" + showCount +
                ", fileName='" + fileName + '\'' +
                ", fileCover='" + fileCover + '\'' +
                ", fileCategory=" + fileCategory +
                ", fileType=" + fileType +
                ", folderType=" + folderType +
                '}';
    }
}
