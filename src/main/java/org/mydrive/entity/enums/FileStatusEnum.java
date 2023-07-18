package org.mydrive.entity.enums;

public enum FileStatusEnum {
    TRANSFER(0, "转码中"),
    TRANSFER_FAIL(1, "转码失败"),
    USING(2, "使用中"),
    RECOVERY(3, "回收站"),
    DEL(4, "已删除");

    FileStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    private Integer status;
    private String desc;



    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
