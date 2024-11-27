package ru.sweetbun.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/error")
public class ErrorController {

    @GetMapping("/stack")
    public ResponseEntity<?> stackOverflow() {
        try {
            causeStackOverflow();
        } catch (StackOverflowError error) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error.getClass());
        }
        return ResponseEntity.ok("OK");
    }

    private void causeStackOverflow() {
        causeStackOverflow();
    }

    @GetMapping("/memory")
    public ResponseEntity<?> outOfMemory() {
        try {
            causeOutOfMemory();
        } catch (OutOfMemoryError error) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error.getClass());
        }
        return ResponseEntity.ok("OK");
    }

    public static void causeOutOfMemory() {
        List<byte[]> memoryLeak = new ArrayList<>();
        while (true) {
            memoryLeak.add(new byte[100 * 1024 * 1024]);
        }
    }
}
