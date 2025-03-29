package com.fpt.shopapp.model;

import com.fpt.shopapp.services.ProductRedisService;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AllArgsConstructor
public class ProductListener {
    private final ProductRedisService productRedisService;
    private static final Logger logger = LoggerFactory.getLogger(ProductListener.class);

    @PrePersist
    public void prePersist(Product product) {
        logger.info("prePersist");
    }

    @PostPersist
    public void postPersist(Product product) {
        logger.info("postPersist");
        productRedisService.clear();
    }

    @PreUpdate
    public void preUpdate(Product product) {
        logger.info("preUpdate");
        productRedisService.clear();
    }

    @PostUpdate
    public void postUpdate(Product product) {
        logger.info("postUpdate");
        productRedisService.clear();
    }

    @PreRemove
    public void preRemove(Product product) {
        logger.info("preRemove");
    }

    @PostRemove
    public void postRemove(Product product) {
        logger.info("postRemove");
        productRedisService.clear();
    }
}
