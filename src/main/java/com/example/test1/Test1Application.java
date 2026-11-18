package com.example.test1;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.awt.Desktop;
import java.net.URI;
import java.util.Locale;

@SpringBootApplication

@MapperScan("com.example.test1.mapper")
public class Test1Application {

    public static void main(String[] args) {
        var context = SpringApplication.run(Test1Application.class, args);


        // Open the hello world home page (/), never the /api endpoints
        try {
            if (context.getEnvironment().getProperty("app.open-browser.enabled", Boolean.class, true)) {
                String port = context.getEnvironment().getProperty("local.server.port",
                        context.getEnvironment().getProperty("server.port", "8080"));
                if (isValidPort(port)) {
                    openBrowser("http://localhost:" + port + "/");
                } else {
                    System.out.println("Unable to open browser automatically: invalid server port " + port);
                }
            }
        } catch (RuntimeException e) {
            System.out.println("Unable to read browser settings: " + e.getMessage());
        }
    }

    private static boolean isValidPort(String port) {
        try {
            int value = Integer.parseInt(port);
            return value > 0 && value <= 65535;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static void openBrowser(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI.create(url));
                System.out.println("Opened browser: " + url);
                return;
            }
        } catch (Exception e) {
            // fall back to the OS command below
            System.out.println("Desktop browse unavailable: " + e.getMessage());
        }

        try {
            String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
            String[] command;
            if (os.contains("win")) {
                command = new String[]{"rundll32", "url.dll,FileProtocolHandler", url};
            } else if (os.contains("mac")) {
                command = new String[]{"open", url};
            } else {
                command = new String[]{"xdg-open", url};
            }
            new ProcessBuilder(command)
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .redirectError(ProcessBuilder.Redirect.DISCARD)
                    .start();
            System.out.println("Opened browser (OS command): " + url);
        } catch (Exception e) {
            System.out.println("Unable to open browser automatically: " + e.getMessage());
        }
    }

}
