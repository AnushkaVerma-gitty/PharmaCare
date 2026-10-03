package com.pharmacy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

// Starting point of the app. Run this file, then open http://localhost:8080
@SpringBootApplication
public class PharmacyApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(PharmacyApplication.class);
        app.setHeadless(false);   // allows opening the browser
        app.run(args);
    }

    // When the app is fully started, open the browser automatically
    @EventListener(ApplicationReadyEvent.class)
    public void openBrowser() {
        String url = "http://localhost:8080";
        System.out.println("\n==============================================");
        System.out.println("  PharmaCare is running!  Open: " + url);
        System.out.println("  Keep this window open. Close it to stop.");
        System.out.println("==============================================\n");
        if ("false".equals(System.getProperty("open.browser"))) return;
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", url).start();
            } else if (os.contains("mac")) {
                new ProcessBuilder("open", url).start();
            } else {
                new ProcessBuilder("xdg-open", url).start();
            }
        } catch (Exception e) {
            // Could not open browser automatically - user can open the URL manually
        }
    }
}
