package org.mydrive.entity.enums;

import org.apache.commons.lang3.ArrayUtils;

public enum FileTypeEnum {
    VIDEO(FileCategoryEnum.VIDEO, 1, new String[]{".mp4", ".avi", ".rmvb", ".mkv", ".mov"}, "video"),
    MUSIC(FileCategoryEnum.MUSIC, 2, new String[]{".mp3", ".flac", ".acc", ".wav", ".wmv"}, "music"),
    IMAGE(FileCategoryEnum.IMAGE, 3, new String[]{".jpg", ".jpeg", ".png", ".gif", ".bmp", ".psd", "webp"}, "image"),
    PDF(FileCategoryEnum.DOC, 4, new String[]{".pdf"}, "pdf"),
    WORD(FileCategoryEnum.DOC, 5, new String[]{".doc", ".docx"}, "word"),
    EXCEL(FileCategoryEnum.DOC, 6, new String[]{".xls", ".xlsx"}, "excel"),
    POWERPOINT(FileCategoryEnum.DOC, 7, new String[]{".ppt", ".pptx"}, "powerpoint"),
    TXT(FileCategoryEnum.DOC, 8, new String[]{".txt"}, "text"),
    PROGRAME(FileCategoryEnum.OTHERS, 9, new String[]{".h", ".c", ".cpp", ".cc", ".m", ".java", ".py", ".html", ".class", ".css", ".js", ".ts", ".sql", ".xml", ".json"}, "code"),
    ZIP(FileCategoryEnum.OTHERS, 10, new String[]{".rar", ".zip", ".7z", ".tar", ".gz", ".bz"}, "zip"),
    EXEC(FileCategoryEnum.OTHERS, 11, new String[]{".exe"}, "exe"),
    OTHERS(FileCategoryEnum.OTHERS, 12, new String[]{}, "others");


    private FileCategoryEnum category;
    private Integer type;
    private String[] suffix;
    private String desc;

    FileTypeEnum(FileCategoryEnum category, Integer type, String[] suffix, String desc) {
        this.category = category;
        this.type = type;
        this.suffix = suffix;
        this.desc = desc;
    }

    public FileCategoryEnum getCategory() {
        return category;
    }

    public Integer getType() {
        return type;
    }

    public String[] getSuffix() {
        return suffix;
    }

    public String getDesc() {
        return desc;
    }

    public static FileTypeEnum getFileTypeBySuffix(String suffix) {
        for (FileTypeEnum item : FileTypeEnum.values()) {
            if (ArrayUtils.contains(item.getSuffix(), suffix)) {
                return item;
            }
        }
        return FileTypeEnum.OTHERS;
    }

    public static FileTypeEnum getByType(Integer type) {
        for (FileTypeEnum item : FileTypeEnum.values()) {
            if (item.getType().equals(type)) {
                return item;
            }
        }
        return null;
    }
}
