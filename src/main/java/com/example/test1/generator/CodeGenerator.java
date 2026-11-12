package com.example.test1.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.TemplateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

public class CodeGenerator {

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/medical_db?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true";
        String username = "root";
        String password = "123456";
        String projectDir = System.getProperty("user.dir");

        FastAutoGenerator.create(url, username, password)
                .globalConfig(builder -> builder
                        .author("student")
                        .outputDir(projectDir + "/src/main/java")
                        .commentDate(""))
                .packageConfig(builder -> builder
                        .parent("com.example.test1")
                        .entity("entity")
                        .mapper("mapper")
                        .service("service")
                        .serviceImpl("service.impl"))
                .strategyConfig(builder -> builder
                        .addInclude("department", "doctor", "patient", "registration", "prescription")
                        .entityBuilder()
                            .enableTableFieldAnnotation()
                            .naming(NamingStrategy.underline_to_camel)
                            .columnNaming(NamingStrategy.underline_to_camel))
                .templateConfig(builder -> builder.disable(TemplateType.CONTROLLER))
                .execute();
    }
}
