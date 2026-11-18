package com.example.test1.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 医疗敏感字段校验单元测试（不依赖 Spring 与数据库，毫秒级执行）。
 */
@DisplayName("敏感字段格式校验")
class FieldValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = {"13900000001", "13812345678", "19900000000"})
    @DisplayName("合法手机号应通过校验")
    void validPhonePasses(String phone) {
        assertDoesNotThrow(() -> FieldValidator.phone(phone, "手机号"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", "12812345678", "1390000000", "139000000011", "abc", "1390000000a"})
    @DisplayName("非法手机号应被拒绝")
    void invalidPhoneRejected(String phone) {
        assertThrows(BizException.class, () -> FieldValidator.phone(phone, "手机号"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"430101199003070016", "110101199212030024", "43010119970913013X",
            "43010119970913013x"})
    @DisplayName("合法身份证号（含小写校验位）应通过校验")
    void validIdCardPasses(String idCard) {
        assertDoesNotThrow(() -> FieldValidator.idCard(idCard));
    }

    @Test
    @DisplayName("身份证校验位错误应被拒绝")
    void wrongCheckDigitRejected() {
        // 把合法号码的校验位改成另一个字符
        assertThrows(BizException.class, () -> FieldValidator.idCard("430101199003070017"));
    }

    @Test
    @DisplayName("身份证出生日期不合法（2 月 30 日）应被拒绝")
    void invalidBirthDateRejected() {
        assertThrows(BizException.class, () -> FieldValidator.idCard("110101199202300024"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"43010119900307001", "4301011990030700161", "010101199003070016",
            "430101199013070016", "4301011990-03-07016"})
    @DisplayName("身份证格式不正确应被拒绝")
    void malformedIdCardRejected(String idCard) {
        assertThrows(BizException.class, () -> FieldValidator.idCard(idCard));
    }

    @Test
    @DisplayName("性别只能为 M 或 F")
    void genderValidation() {
        assertDoesNotThrow(() -> FieldValidator.gender("M"));
        assertDoesNotThrow(() -> FieldValidator.gender("F"));
        assertThrows(BizException.class, () -> FieldValidator.gender("X"));
        assertThrows(BizException.class, () -> FieldValidator.gender("男"));
        assertThrows(BizException.class, () -> FieldValidator.gender(null));
    }

    @Test
    @DisplayName("固定电话允许数字与连字符")
    void officePhoneValidation() {
        assertDoesNotThrow(() -> FieldValidator.officePhone("0731-8801001", "科室电话"));
        assertDoesNotThrow(() -> FieldValidator.officePhone("07318801001", "科室电话"));
        assertThrows(BizException.class, () -> FieldValidator.officePhone("abc", "科室电话"));
        assertThrows(BizException.class, () -> FieldValidator.officePhone("", "科室电话"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    @DisplayName("空白值应被判定为空")
    void blankRejected(String value) {
        assertThrows(BizException.class, () -> FieldValidator.notBlank(value, "姓名"));
    }

    @Test
    @DisplayName("超长字段应被拒绝")
    void tooLongRejected() {
        String tooLong = "a".repeat(201);
        assertThrows(BizException.class, () -> FieldValidator.maxLength(tooLong, 200, "地址"));
        assertDoesNotThrow(() -> FieldValidator.maxLength("a".repeat(200), 200, "地址"));
        assertDoesNotThrow(() -> FieldValidator.maxLength(null, 200, "地址"));
    }

    // ------------------------------------------------------------------
    // LIKE 通配符转义
    // ------------------------------------------------------------------

    @Test
    @DisplayName("LIKE 通配符应被转义为字面量")
    void likeWildcardEscaped() {
        assertEquals("张!_", SqlLikeUtil.escape("张_"));
        assertEquals("!%", SqlLikeUtil.escape("%"));
        assertEquals("a!!b", SqlLikeUtil.escape("a!b"));
        assertEquals("正常姓名", SqlLikeUtil.escape("正常姓名"));
        assertEquals("张三", SqlLikeUtil.escape("  张三  "));
        // 反斜杠不是本工具的转义字符，应原样保留
        assertEquals("a\\b", SqlLikeUtil.escape("a\\b"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  "})
    @DisplayName("空关键字返回 null，表示不追加该查询条件")
    void emptyKeywordReturnsNull(String keyword) {
        assertNull(SqlLikeUtil.escape(keyword));
    }
}
