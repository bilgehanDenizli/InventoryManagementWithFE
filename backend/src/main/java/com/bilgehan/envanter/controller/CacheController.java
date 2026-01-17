package com.bilgehan.envanter.controller;

import com.bilgehan.envanter.kafka.producer.KafkaEvent;
import com.bilgehan.envanter.kafka.producer.KafkaProducer;
import com.bilgehan.envanter.model.request.CacheDeleteByNameRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cache")
public class CacheController {
    private final KafkaEvent kafkaEvent;


    public CacheController(KafkaEvent kafkaEvent) {
        this.kafkaEvent = kafkaEvent;
    }

    @PostMapping("/flushAll")
    public void flushAll() {
        kafkaEvent.deleteCache();
    }

    @PostMapping("/flushByName")
    public void flushByName(@RequestBody CacheDeleteByNameRequest request){
        kafkaEvent.deleteCacheByName(request.getCacheName());
    }
}
