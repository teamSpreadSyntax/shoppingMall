package home.project.service.util;

public final class RegexPatterns {
    public static final String EMAIL_PATTERN =
            "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])+\\.[a-zA-Z]{2,3}$";
    public static final String PASSWORD_PATTERN =
            "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{12,}$";
    public static final String PHONE_PATTERN =
            "^01(?:0|1|[6-9])[-]?(?:\\d{3}|\\d{4})[-]?\\d{4}$";
    public static final String EMAIL_MESSAGE = "이메일 형식이 올바르지 않습니다.";
    public static final String PASSWORD_MESSAGE = "비밀번호는 대문자, 소문자, 숫자, 특수문자를 포함한 12자 이상이어야 합니다.";
    public static final String PHONE_MESSAGE = "전화번호 형식이 올바르지 않습니다.";
    private RegexPatterns() {
    }
}