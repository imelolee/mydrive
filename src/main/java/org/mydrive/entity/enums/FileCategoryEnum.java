package org.mydrive.entity.enums;

public enum FileCategoryEnum {
    VIDEO(1, "video", "视频"),
    MUSIC(1, "music", "音乐"),
    IMAGE(3, "image", "图片"),
    DOC(4, "doc", "文档"),
    OTHERS(5, "others", "其他");

    private Integer category;
    private String code;
    private String desc;

    FileCategoryEnum(Integer category, String code, String desc) {
        this.category = category;
        this.code = code;
        this.desc = desc;
    }

    public static FileCategoryEnum getByCode(String code){
        for (FileCategoryEnum value : FileCategoryEnum.values()) {
            if(value.getCode().equals(code)){
                return value;
            }
        }
        return null;
    }

    public Integer getCategory() {
        return category;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
