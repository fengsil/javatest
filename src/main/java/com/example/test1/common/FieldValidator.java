package com.example.test1.common;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public final class FieldValidator {

    private static final Pattern PHONE = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern OFFICE_PHONE = Pattern.compile("^[0-9][0-9\\-]{6,19}$");
    private static final Pattern ID_CARD = Pattern.compile("^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$");
    private static final int[] WEIGHTS = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
    private static final char[] CHECK_CODES = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

    private FieldValidator() {
    }

    public static void notBlank(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new BizException(label + "不能为空");
        }
    }

    public static void phone(String value, String label) {
        if (value == null || !PHONE.matcher(value.trim()).matches()) {
            throw new BizException(label + "格式不正确，应为 11 位中国大陆手机号");
        }
    }

    public static void optionalOfficePhone(String value, String label) {
        if (value != null && !value.trim().isEmpty()) {
            String v = value.trim();
            if (!OFFICE_PHONE.matcher(v).matches()) {
                throw new BizException(label + "格式不正确，应为 7-20 位数字或连字符");
            }
        }
    }

    public static void idCard(String value) {
        if (value == null || !ID_CARD.matcher(value.trim()).matches()) {
            throw new BizException("身份证号格式不正确，应为 18 位居民身份证号码");
        }
        String id = value.trim().toUpperCase();
        try {
            LocalDate.parse(id.substring(6, 14), DateTimeFormatter.BASIC_ISO_DATE);
        } catch (DateTimeParseException e) {
            throw new BizException("身份证号中的出生日期不合法");
        }
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (id.charAt(i) - '0') * WEIGHTS[i];
        }
        if (id.charAt(17) != CHECK_CODES[sum % 11]) {
            throw new BizException("身份证号校验位不正确");
        }
    }
}