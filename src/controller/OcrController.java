package com.example.demo.controller;

import com.example.demo.service.OcrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/identity")
public class OcrController {

    @Autowired
    private OcrService ocrService;

    // Frontend gọi vào API này
    @PostMapping("/extract")
    public ResponseEntity<?> extractInfo(@RequestParam("image") MultipartFile file) {
        // Gọi Service xử lý
        String result = ocrService.callPythonAiService(file);
        return ResponseEntity.ok(result);
    }
}