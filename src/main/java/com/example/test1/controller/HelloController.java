package com.example.test1.controller;

import com.example.test1.mapper.StatusMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 环境自检接口：验证 Spring Boot 启动是否正常、MyBatis 与数据库是否连通。
 *
 * <p>注意：本控制器刻意保持"扁平响应结构"（直接返回 Map，不套 {@code R<T>}），
 * 因为前端 StartupView 直接读取 {@code status.mybatis} / {@code status.database} 等顶层字段，
 * 若改为统一包装会导致前端状态面板全部显示"检测中"。</p>
 */
@RestController
public class HelloController {

    private final StatusMapper statusMapper;

    public HelloController(StatusMapper statusMapper) {
        this.statusMapper = statusMapper;
    }

    /** 首页：纯文本 hello world，启动后由浏览器自动打开，便于确认服务可用。 */
    @GetMapping(value = "/", produces = "text/plain;charset=UTF-8")
    public String root() {
        return "hello world";
    }

    /** 返回 JSON 形式的 hello world 与服务器当前时间。 */
    @GetMapping("/api/hello")
    public Map<String, Object> hello() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("message", "hello world");
        result.put("time", LocalDateTime.now().toString());
        return result;
    }

    /** 健康检查：返回 MyBatis 连通状态与当前使用的数据库名。 */
    @GetMapping("/api/status")
    public Map<String, Object> status() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("springBoot", true);

        boolean mybatisOk = false;
        String database = "unknown";
        try {
            Integer one = statusMapper.checkConnection();
            mybatisOk = (one != null && one == 1);
            database = statusMapper.databaseName();
        } catch (Exception e) {
            result.put("mybatisError", e.getMessage());
        }
        result.put("mybatis", mybatisOk);
        result.put("database", database);
        result.put("time", LocalDateTime.now().toString());
        return result;
    }
}
