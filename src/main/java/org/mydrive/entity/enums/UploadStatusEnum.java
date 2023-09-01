package org.mydrive.entity.enums;

public enum UploadStatusEnum {
    UPLOAD_SECONDS("upload_seconds", "fast upload"),
    UPLOADING("uploading", "uploading"),
    UPLOAD_FINISH("upload_finish", "upload finished");

    private String code;
    private String desc;

    UploadStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
