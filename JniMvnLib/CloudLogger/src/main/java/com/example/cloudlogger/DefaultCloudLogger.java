package com.example.cloudlogger;

import com.example.cloudlogger.bridge.CloudLogger;

import java.time.Instant;

public class DefaultCloudLogger extends CloudLogger {

    @Override
    public void upload(String category, String message) {
        // Demo: replace with real cloud SDK/API upload.
        System.out.printf("[CloudLogger][%s][%s] %s%n", Instant.now(), category, message);
    }
}
