package org.mydrive.entity.enums;


public enum ResponseCodeEnum {
    CODE_200(200, "成功したリクエスト"),
    CODE_404(404, "リクエストアドレスが存在しません"),
    CODE_600(600, "リクエストパラメータエラー"),
    CODE_601(601, "情報はすでに存在します"),
    CODE_500(500, "サーバーがエラーを返しました"),
    CODE_901(901, "ログインがタイムアウトしました，再度ログインしてください"),
    CODE_902(902, "共有リンクが存在しないか、有効期限が切れています"),

    CODE_903(903, "共有の検証が無効です，再検証してください"),

    CODE_904(904, "スペース不足");


    private Integer code;

    private String msg;

    ResponseCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
