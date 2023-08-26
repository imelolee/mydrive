package org.mydrive.entity.enums;

public enum FileStatusEnum {
    TRANSFER(0, "Transfer"),
    TRANSFER_FAIL(1, "Transfer failed"),
    USING(2, "Using"),
    RECOVERY(3, "Recovery"),
    DEL(4, "Deleted");

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
