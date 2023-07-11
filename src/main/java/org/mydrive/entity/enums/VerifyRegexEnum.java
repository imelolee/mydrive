package org.mydrive.entity.enums;

public enum VerifyRegexEnum {
    NO("", "不校验"),
    EMAIL("^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$", "邮箱"),
    PASSWORD("^(?=.*[a-zA-Z])(?=.*\\d).{8,18}$", "密码为数字，字母，特殊字符，长度为8-18位");


    private String regex;
    private String desc;

    VerifyRegexEnum(String regex, String desc){
        this.regex = regex;
        this.desc = desc;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
