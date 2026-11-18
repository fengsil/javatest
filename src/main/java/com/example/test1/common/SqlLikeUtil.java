package com.example.test1.common;

/**
 * LIKE 模糊查询条件转义工具。
 *
 * <p>用户在搜索框输入 {@code %} 或 {@code _} 时，若不转义会被 SQL 当成通配符，
 * 例如搜索「张_」会匹配所有张姓医生，搜索「%」会匹配全部记录——既造成查询结果错误，
 * 也让攻击者可以构造宽泛匹配放大查询代价。</p>
 *
 * <p><b>为什么转义字符用 {@code !} 而不是反斜杠：</b>
 * MySQL 的字符串字面量中反斜杠本身是转义符，SQL 里写 {@code ESCAPE '\'} 会被解析成
 * 非法转义序列导致语法错误（实测报 500）。而 {@code !} 在任何 sql_mode 下都无需二次转义，
 * 与 Mapper XML 中声明的 {@code ESCAPE '!'} 保持一致即可。</p>
 *
 * <p>注意：转义只改变 LIKE 的匹配语义，参数仍然通过 {@code #{}} 预编译绑定，
 * 与 SQL 注入防护是两件事，不能互相替代。</p>
 */
public final class SqlLikeUtil {

    /** LIKE 转义字符，必须与 Mapper XML 中的 ESCAPE '!' 保持一致 */
    public static final char ESCAPE = '!';

    private SqlLikeUtil() {
    }

    /**
     * 转义 LIKE 模式中的特殊字符，空白输入返回 null 表示“不加这个条件”。
     */
    public static String escape(String keyword) {
        if (keyword == null) {
            return null;
        }
        String trimmed = keyword.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder(trimmed.length() + 8);
        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            if (c == ESCAPE || c == '%' || c == '_') {
                sb.append(ESCAPE);
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
