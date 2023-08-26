package org.mydrive.entity.enums;

public enum FileCategoryEnum {
    VIDEO(1, "video", "Video"),
    MUSIC(2, "music", "Music"),
    IMAGE(3, "image", "Image"),
    DOC(4, "doc", "Document"),
    OTHERS(5, "others", "Others");

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
