package com.example.backend;

import com.example.foundation.FoundationMath;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/math")
public class MathController {

    static {
        String explicitPath = System.getenv("FOUNDATION_JNI_LIB");
        if (explicitPath != null && !explicitPath.isBlank() && Files.exists(Path.of(explicitPath))) {
            System.load(explicitPath);
        } else {
            System.loadLibrary("foundation_jni");
        }
    }

    private final FoundationMath foundationMath = new FoundationMath();

    @GetMapping("/add")
    public AddResponse add(@RequestParam int a, @RequestParam int b) {
        int result = foundationMath.add(a, b);
        return new AddResponse(a, b, result);
    }

    public record AddResponse(int a, int b, int result) {
    }
}
