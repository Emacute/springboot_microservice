package com.example.messaging.controller;

import com.example.messaging.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    //Endpoint to upload Excel file
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {

        messageService.uploadFile(file);

        return ResponseEntity.ok("File processed successfully");
    }
}
