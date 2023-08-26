package org.mydrive.entity.enums;

public enum VerifyRegexEnum {
    NO("", "No verify"),
    EMAIL("^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$", "email"),
    PASSWORD("^(?=.*[a-zA-Z])(?=.*\\d).{8,18}$", "The password should be numbers, letters, special characters, and the length is 8-18 digits");


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
