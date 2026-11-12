package com.example.test1;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.awt.Desktop;
import java.net.URI;

@SpringBootApplication
@MapperScan("com.example.test1.mapper")
public class Test1Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Test1Application.class, args);

        // Open the hello world home page (/), never the /api endpoints
        if (context.getEnvironment().getProperty("app.open-browser.enabled", Boolean.class, true)) {
            String port = context.getEnvironment().getProperty("server.port", "8080");
            openBrowser("http://localhost:" + port + "/");
        }
    }

    private static void openBrowser(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI.create(url));
                System.out.println("Opened browser: " + url);
                return;
            }
        } catch (Exception ignored) {
            // fall back to the OS command below
        }

        try {
            String os = System.getProperty("os.name", "").toLowerCase();
            if (os.contains("win")) {
                Runtime.getRuntime().exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", url});
            } else if (os.contains("mac")) {
                Runtime.getRuntime().exec(new String[]{"open", url});
            } else {
                Runtime.getRuntime().exec(new String[]{"xdg-open", url});
            }
            System.out.println("Opened browser (OS command): " + url);
        } catch (Exception e) {
            System.out.println("Unable to open browser automatically: " + e.getMessage());
        }
    }

}
