package com.example.backend;

import com.example.foundation.FoundationMath;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/math")
public class MathController {

    private final FoundationMath foundationMath = new FoundationMath();

    @GetMapping("/add")
    public AddResponse add(@RequestParam int a, @RequestParam int b) {
        int result = foundationMath.add(a, b);
        return new AddResponse(a, b, result);
    }

    public record AddResponse(int a, int b, int result) {
    }
}
