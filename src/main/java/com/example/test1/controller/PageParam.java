package com.example.test1.controller;

import com.example.test1.common.BizException;

/**
 * 分页参数对象：统一校验页码与每页条数，并计算 LIMIT 偏移量。
 */
final class PageParam {

    /** 每页最大条数，防止一次查出全表数据拖垮服务 */
    private static final long MAX_SIZE = 100;
    /** 页码上限：避免 (page-1)*size 溢出 long 后变成负数导致 LIMIT 语法错误 */
    private static final long MAX_PAGE = 1_000_000L;

    final long page;
    final long size;
    final long offset;

    private PageParam(long page, long size) {
        this.page = page;
        this.size = size;
        this.offset = (page - 1) * size;
    }

    static PageParam of(long page, long size) {
        if (page < 1) {
            throw new BizException("页码必须大于 0");
        }
        if (page > MAX_PAGE) {
            throw new BizException("页码超出允许范围（最大 " + MAX_PAGE + "）");
        }
        if (size < 1 || size > MAX_SIZE) {
            throw new BizException("每页条数必须在 1 到 " + MAX_SIZE + " 之间");
        }
        return new PageParam(page, size);
    }
}
