package org.mydrive.entity.constants;

public class Constants {
    public static final Integer ZERO = 0;

    public static final Integer LENGTH_5 = 5;

    public static final Integer LENGTH_10 = 10;

    public static final Long MB = 1024*1024L;

    public static final String FILE_FOLDER_FILE = "/file/";

    public static final String FILE_FOLDER_TEMP = "/temp/";

    public static final String FILE_FOLDER_AVATAR = "avatar/";

    public static final String AVATAR_SUFFIX = ".jpg";

    public static final String AVATAR_DEFAULT = "default_avatar.jpg";

    public static final String CHECK_CODE_KEY = "check_code_key";
    public static final String CHECK_CODE_KEY_EMAIL = "check_code_key_email";

    public static final String REDIS_KEY_SYS_SETTING = "mydrive:syssetting:";

    public static final String REDIS_KEY_USER_SPACE_USE = "mydrive:user:spaceuse:";

    public static final String REDIS_KEY_USER_FILE_TEMP_SIZE = "mydrive:user:file:temp:";

    public static final Integer REDIS_KEY_EX_ONE_MIN = 60;

    public static final Integer REDIS_KEY_EX_ONE_HOUR = REDIS_KEY_EX_ONE_MIN * 60;


    public static final Integer REDIS_KEY_EX_ONE_DAY = REDIS_KEY_EX_ONE_MIN * 60 * 24;

    public static final String SESSION_KEY = "session_key";


    public static final String TS_NAME = "index.ts";
}
