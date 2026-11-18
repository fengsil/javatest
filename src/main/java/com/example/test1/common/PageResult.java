package com.example.test1.common;

import java.util.List;

/**
 * 统一分页返回结构：records 当前页数据、total 总记录数、page 当前页码、size 每页条数、pages 总页数。
 */
public class PageResult<T> {

    private List<T> records;
    private long total;
    private long page;
    private long size;
    private long pages;

    public PageResult(List<T> records, long total, long page, long size) {
        this.records = records;
        this.total = total;
        this.page = page;
        this.size = size;
        this.pages = size == 0 ? 0 : (total + size - 1) / size;
    }

    public List<T> getRecords() {
        return records;
    }

    public long getTotal() {
        return total;
    }

    public long getPage() {
        return page;
    }

    public long getSize() {
        return size;
    }

    public long getPages() {
        return pages;
    }
}
