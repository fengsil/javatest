package com.example.test1.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.annotation.IdType;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

/**
 * MyBatis-Plus 代码生成器：一键生成 8 张医疗业务表的 entity / mapper / service / mapper.xml。
 *
 * <p>运行方式（项目根目录）：</p>
 * <pre>
 * .\mvnw.cmd -q org.codehaus.mojo:exec-maven-plugin:3.3.0:java "-Dexec.mainClass=com.example.test1.generator.CodeGenerator"
 * </pre>
 *
 * <p>说明：生成结果先输出到 target/generated-mp/ 目录做验收留痕，避免每次执行都会覆盖
 * src/main/java 中已经人工补充过的实体与 Mapper；确认无误后再手工同步到源码目录。</p>
 */
public final class CodeGenerator {

    private static final String URL =
            "jdbc:mysql://localhost:3306/medical_db?useUnicode=true&characterEncoding=UTF-8"
                    + "&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";

    /** 需要生成持久层代码的业务表（8 张）。 */
    private static final String[] TABLES = {
            "department", "job_title", "drug", "doctor", "patient",
            "registration", "prescription", "prescription_item"
    };

    private CodeGenerator() {
    }

    public static void main(String[] args) throws Exception {
        Path classesDir = Paths.get(CodeGenerator.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI());
        String projectDir = classesDir.resolve("../..").normalize().toString();
        String outputDir = projectDir + "/target/generated-mp";

        FastAutoGenerator.create(URL, USERNAME, PASSWORD)
                .globalConfig(builder -> builder
                        .author("student")
                        .outputDir(outputDir + "/src/main/java")
                        .commentDate("")
                        .disableOpenDir())
                .packageConfig(builder -> builder
                        .parent("com.example.test1")
                        .entity("entity")
                        .mapper("mapper")
                        .service("service")
                        .serviceImpl("service.impl")
                        .xml("mapper")
                        .pathInfo(Collections.singletonMap(OutputFile.xml, outputDir + "/src/main/resources/mapper")))
                .strategyConfig(builder -> {
                    builder.addInclude(TABLES)
                            .entityBuilder()
                            .idType(IdType.AUTO)
                            .enableTableFieldAnnotation()
                            .naming(NamingStrategy.underline_to_camel)
                            .columnNaming(NamingStrategy.underline_to_camel)
                            .formatFileName("%s");
                    builder.mapperBuilder().enableBaseResultMap();
                    // 控制器承载分页、参数校验、外键存在性判断等业务逻辑，不生成通用模板
                    builder.controllerBuilder().disable();
                })
                .execute();

        System.out.println("MyBatis-Plus 代码生成完成，输出目录：" + outputDir);
    }
}
