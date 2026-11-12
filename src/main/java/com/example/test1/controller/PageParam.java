package com.example.test1.controller;

import com.example.test1.common.BizException;

final class PageParam {

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
        if (size < 1 || size > 100) {
            throw new BizException("每页条数必须在 1 到 100 之间");
        }
        return new PageParam(page, size);
    }
}