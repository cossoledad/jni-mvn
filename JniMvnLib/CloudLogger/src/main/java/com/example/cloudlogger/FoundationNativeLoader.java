package com.example.cloudlogger;

import java.nio.file.Files;
import java.nio.file.Path;

final class FoundationNativeLoader {

    private FoundationNativeLoader() {
    }

    static void loadFoundationJniLibrary() {
        String explicitPath = System.getenv("FOUNDATION_JNI_LIB");
        if (explicitPath != null && !explicitPath.isBlank() && Files.exists(Path.of(explicitPath))) {
            System.load(explicitPath);
        } else {
            System.loadLibrary("foundation_jni");
        }
    }
}
