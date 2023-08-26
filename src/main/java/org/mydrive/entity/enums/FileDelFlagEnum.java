package org.mydrive.entity.enums;

public enum FileDelFlagEnum {
    RECYCLE(1, "Recycle"),
    USING(2, "Using");

    private Integer flag;
    private String desc;

    FileDelFlagEnum(Integer flag, String desc) {
        this.flag = flag;
        this.desc = desc;
    }

    public Integer getFlag() {
        return flag;
    }

    public String getDesc() {
        return desc;
    }
}
