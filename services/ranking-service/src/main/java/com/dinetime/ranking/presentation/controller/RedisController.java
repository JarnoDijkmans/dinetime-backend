package com.dinetime.ranking.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/set")
    public String setKeyValue() {
        redisTemplate.opsForValue().set("myKey", "Hello Redis!");
        return "Key set!";
    }

    @GetMapping("/get")
    public String getValue() {
        return (String) redisTemplate.opsForValue().get("myKey");
    }

    @DeleteMapping("/flush")
    public ResponseEntity<String> flushRedis() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
        return ResponseEntity.ok("Redis cache cleared successfully!");
    }
}
