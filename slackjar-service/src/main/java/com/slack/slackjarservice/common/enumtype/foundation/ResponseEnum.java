package com.slack.slackjarservice.common.enumtype.foundation;

import lombok.Getter;

@Getter
public enum ResponseEnum {

    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),
    PARAM_ERROR(400, "参数错误"),
    UN_LOGIN(401, "未登录"),
    NO_PERMISSION(403, "暂无操作权限"),
    DATA_NOT_EXIST(404, "数据不存在"),
    DATA_NOT_EXISTS(404, "数据不存在"),
    NOT_FOUND(404, "未找到"),
    SHARE_EXPIRED(410, "分享已过期"),
    USER_NOT_EXIST(40401, "用户不存在"),
    USERNAME_EXIST(40001, "用户名已存在"),
    EMAIL_EXIST(40002, "邮箱已存在"),
    PHONE_EXIST(40003, "手机号已存在"),
    PASSWORD_ERROR(40004, "密码错误"),
    CAPTCHA_ERROR(40005, "验证码错误"),
    OLD_PASSWORD_ERROR(40006, "原密码错误"),
    ORIGINAL_PASSWORD_ERROR(40006, "原密码错误"),
    PASSWORD_NOT_MATCH(40007, "两次输入的密码不一致"),
    NO_PERMISSION_LOGIN_INFO(40301, "无权限查看登录信息"),
    SOCKET_BIZ_NOT_FOUND(40402, "业务数据不存在"),
    RSA_LOAD_PRIVATE_KEY_ERROR(50001, "加载私钥失败"),
    RSA_LOAD_PUBLIC_KEY_ERROR(50020, "加载公钥失败"),
    FILE_READER_STREAM(50021, "文件读取失败"),
    RATE_LIMIT(429, "系统繁忙，请稍后再试"),
    SERVICE_DEGRADE(503, "服务降级"),
    AI_CHAT_MESSAGE_TYPE(40008, "消息类型错误"),
    SSL_CERT_SOURCE_INVALID(40009, "证书来源无效"),
    FILE_BIZ_TYPE(40010, "业务类型错误"),
    FILE_FORMAT_NOT_ALLOWED(40011, "文件格式不允许"),
    FILE_EXCEED(40012, "文件大小超出限制"),
    RSA_DECRYPT_ERROR(50002, "RSA解密失败"),
    RSA_ENCRYPT_ERROR(50003, "RSA加密失败"),
    FILE_URL_INVALID(40013, "文件URL无效"),
    AI_CHAT_MESSAGE_LENGTH(40014, "消息长度超出限制"),
    AI_CONFIG(50004, "AI配置错误"),
    AI_CONFIG_REFRESH(50005, "AI配置刷新失败"),
    AI_API_SERVER(50006, "AI服务调用失败"),
    AI_CHAT_STREAM_SSE_TIMEOUT(50007, "AI聊天超时"),
    FILE_STORAGE_STRATEGY(50008, "文件存储策略错误"),
    FILE_UPLOAD(50009, "文件上传失败"),
    FILE_DOWNLOAD(50010, "文件下载失败"),
    FILE_SIGNATURE(50011, "文件签名失败"),
    FILE_DELETE(50012, "文件删除失败"),
    FILE_CALLBACK(50013, "文件回调失败"),
    FILE_DOMAIN(50014, "文件域名配置错误"),
    AI_CHAT_PROCESS(50015, "AI聊天处理失败"),
    PERMISSION_CODE_NOT_ALLOW_MODIFY(40015, "权限编码不允许修改"),
    PERMISSION_CODE_REPEAT(40016, "权限编码重复"),
    PERMISSION_CODE_EXITS_SUB_LEVEL(40017, "存在子级权限"),
    ROLE_CODE_NOT_ALLOW_MODIFY(40018, "角色编码不允许修改"),
    ROLE_SYSTEM_NOT_ALLOW_DISABLE(40019, "系统角色不允许禁用"),
    ROLE_CODE_REPEAT(40020, "角色编码重复"),
    ROLE_SYSTEM_NOT_ALLOW_DELETE(40021, "系统角色不允许删除"),
    USER_DISABLED(40022, "用户已禁用"),
    SSL_CERT_UPLOAD_VERIFY_FAIL(50016, "证书上传验证失败"),
    SSL_CERT_SFTP_UPLOAD_FAIL(50017, "证书SFTP上传失败"),
    SSL_CERT_SFTP_CONNECT_FAIL(50018, "证书SFTP连接失败"),
    SHELL_COMMAND_ERROR(50019, "Shell命令执行失败"),
    SSL_CERT_SERVER_CONFIG_EMPTY(40023, "证书服务器配置为空"),
    DATA_EXISTS(40024, "数据已存在"),
    DICT_CODE_NOT_ALLOW_MODIFY(40025, "字典编码不允许修改"),
    DICT_CODE_REPEAT(40026, "字典编码重复"),
    DICT_CODE_ITEM_REPEAT(40027, "字典项值重复"),
    DICT_CODE_NOT_EXIST(40028, "字典编码不存在"),
    FILE_NOT_EMPTY(40029, "文件不能为空"),
    FILE_NOT_EXIST(40030, "文件不存在"),
    FILE_ACCESS_NOT(40031, "文件访问失败"),
    USER_EXIST(40032, "用户已存在"),
    ;

    private final int code;
    private final String message;

    ResponseEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}