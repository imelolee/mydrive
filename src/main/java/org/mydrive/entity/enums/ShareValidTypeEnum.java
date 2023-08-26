package org.mydrive.entity.enums;

public enum ShareValidTypeEnum {
    DAY_1(0, 1, "1 day"),
    DAY_7(1, 7, "7 days"),
    DAY_30(2, 30, "30 days"),
    FOREVER(3, -1, "Forever valid");

    private Integer type;
    private Integer days;
    private String desc;

    ShareValidTypeEnum(Integer type, Integer days, String desc) {
        this.type = type;
        this.days = days;
        this.desc = desc;
    }

    public static ShareValidTypeEnum getByType(Integer type) {
        for (ShareValidTypeEnum typeEnums : ShareValidTypeEnum.values()) {
            if (typeEnums.getType().equals(type)) {
                return typeEnums;
            }
        }
        return null;
    }

    public Integer getType() {
        return type;
    }

    public Integer getDays() {
        return days;
    }

    public String getDesc() {
        return desc;
    }
}
