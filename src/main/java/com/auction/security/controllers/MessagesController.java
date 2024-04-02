package com.auction.security.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class MessagesController {

    @GetMapping("/admin")
    public ResponseEntity<List<String>> admin() {

        return ResponseEntity.ok(Arrays.asList("admin 1", "admin 2"));
    }

    @GetMapping("/user")
    public ResponseEntity<List<String>> user() {

        return ResponseEntity.ok(Arrays.asList("user 1", "user 2"));
    }
}
