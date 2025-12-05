package com.study_linker.system.controller;

import com.study_linker.system.service.SystemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
public class SystemController {

    private final SystemService systemService;

    // 🔥 백업 스냅샷 생성
    @PostMapping("/backup")
    public ResponseEntity<String> createBackup() {
        systemService.createBackup();
        return ResponseEntity.ok("Backup snapshot process executed.");
    }

    // 🔥 캐시 무효화
    @PostMapping("/cache/clear")
    public ResponseEntity<String> clearCache() {
        systemService.clearCache();
        return ResponseEntity.ok("Cache clear executed.");
    }
}