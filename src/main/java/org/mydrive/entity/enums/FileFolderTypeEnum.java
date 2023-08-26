package org.mydrive.entity.enums;

public enum FileFolderTypeEnum {
    FILE(0, "File"),
    FOLDER(1, "Folder");

    private Integer type;
    private String desc;

    FileFolderTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
