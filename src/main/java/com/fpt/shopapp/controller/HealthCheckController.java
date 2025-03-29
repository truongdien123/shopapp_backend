package com.fpt.shopapp.controller;

import com.fpt.shopapp.model.Category;
import com.fpt.shopapp.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/healthcheck")
@RequiredArgsConstructor
public class HealthCheckController {
    private final CategoryService categoryService;

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        try {
            List<Category> categories = categoryService.getAllCategories();
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("failed");
        }
    }
}
