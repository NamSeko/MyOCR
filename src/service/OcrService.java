package com.example.demo.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class OcrService {

    // URL của Python Service
    private final String AI_SERVICE_URL = "http://localhost:8000/ocr";

    public String callPythonAiService(MultipartFile file) {
        RestTemplate restTemplate = new RestTemplate();

        try {
            // 1. Tạo Header: báo là gửi form-data
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // 2. Xử lý File để gửi đi (Quan trọng: Phải override getFilename)
            // Nếu dùng ByteArrayResource thường, Spring sẽ không gửi tên file, gây lỗi bên Python
            ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            // 3. Đóng gói body (Key "file" phải trùng với bên Python: file: UploadFile)
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);

            // 4. Tạo Request
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // 5. Bắn sang Python và nhận kết quả
            ResponseEntity<String> response = restTemplate.postForEntity(AI_SERVICE_URL, requestEntity, String.class);

            return response.getBody(); // Trả về JSON từ Python (id, name...)

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Lỗi khi gọi AI Service\"}";
        }
    }
}