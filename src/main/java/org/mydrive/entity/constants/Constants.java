package org.mydrive.entity.constants;

public class Constants {
    public static final Integer ZERO = 0;

    public static final Integer LENGTH_5 = 5;

    public static final Integer LENGTH_10 = 10;

    public static final Long MB = 1024*1024L;


    public static final String CHECK_CODE_KEY = "check_code_key";
    public static final String CHECK_CODE_KEY_EMAIL = "check_code_key_email";

    public static final String REDIS_KEY_SYS_SETTING = "mydrive:syssetting:";

    public static final String REDIS_KEY_USER_SPACE_USE = "mydrive:user:spaceuse:";

    public static final Integer REDIS_KEY_EX_ONE_MIN = 60;

    public static final Integer REDIS_KEY_EX_ONE_DAY = REDIS_KEY_EX_ONE_MIN * 60 * 24;

    public static final String SESSION_KEY = "session_key";



}
