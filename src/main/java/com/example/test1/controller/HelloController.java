package com.example.test1.controller;

import com.example.test1.mapper.StatusMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HelloController {

    private final StatusMapper statusMapper;

    public HelloController(StatusMapper statusMapper) {
        this.statusMapper = statusMapper;
    }

    @GetMapping(value = "/", produces = "text/plain;charset=UTF-8")
    public String root() {
        return "hello world";
    }

    @GetMapping("/api/hello")
    public Map<String, Object> hello() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("message", "hello world");
        result.put("time", LocalDateTime.now().toString());
        return result;
    }

    @GetMapping("/api/status")
    public Map<String, Object> status() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("springBoot", true);

        boolean mybatisOk = false;
        String dbProduct = "unknown";
        try {
            Integer one = statusMapper.checkConnection();
            mybatisOk = (one != null && one == 1);
            dbProduct = statusMapper.databaseName();
        } catch (Exception e) {
            result.put("mybatisError", e.getMessage());
        }
        result.put("mybatis", mybatisOk);
        result.put("database", dbProduct);
        result.put("time", LocalDateTime.now().toString());
        return result;
    }
}
