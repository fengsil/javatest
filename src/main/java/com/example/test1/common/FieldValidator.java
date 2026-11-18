package com.example.test1.common;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * 医疗敏感字段格式校验工具：
 * 手机号、身份证号（含出生日期与 ISO 7064 MOD 11-2 校验位）、固定电话、性别、长度上限。
 */
public final class FieldValidator {

    /** 中国大陆 11 位手机号 */
    private static final Pattern PHONE = Pattern.compile("^1[3-9]\\d{9}$");
    /** 固定电话：7-20 位，首位为数字，其余为数字或连字符 */
    private static final Pattern OFFICE_PHONE = Pattern.compile("^[0-9][0-9\\-]{6,19}$");
    /** 18 位居民身份证：6 位地区码 + 8 位出生日期 + 3 位顺序码 + 1 位校验位 */
    private static final Pattern ID_CARD = Pattern.compile(
            "^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$");
    /** 身份证校验位权重 */
    private static final int[] WEIGHTS = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
    /** 加权和模 11 对应的校验码 */
    private static final char[] CHECK_CODES = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

    private FieldValidator() {
    }

    public static void notBlank(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new BizException(label + "不能为空");
        }
    }

    public static void maxLength(String value, int max, String label) {
        if (value != null && value.length() > max) {
            throw new BizException(label + "长度不能超过 " + max + " 个字符");
        }
    }

    public static void phone(String value, String label) {
        if (value == null || !PHONE.matcher(value.trim()).matches()) {
            throw new BizException(label + "格式不正确，应为 11 位中国大陆手机号");
        }
    }

    public static void officePhone(String value, String label) {
        notBlank(value, label);
        if (!OFFICE_PHONE.matcher(value.trim()).matches()) {
            throw new BizException(label + "格式不正确，应为 7-20 位数字或连字符");
        }
    }

    /** 性别：M 男 / F 女，与数据库 CHECK 约束保持一致。 */
    public static void gender(String value) {
        if (value == null || (!"M".equals(value) && !"F".equals(value))) {
            throw new BizException("性别格式不正确，只能为 M（男）或 F（女）");
        }
    }

    /** 身份证：格式 + 出生日期真实性 + 校验位。 */
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
