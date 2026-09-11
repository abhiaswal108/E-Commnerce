package com.aa.ecommerce.scheduler;

import com.aa.ecommerce.entity.Product;
import com.aa.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class LowStockScheduler {

    private final ProductRepository productRepository;
    private static final Logger log = LoggerFactory.getLogger(LowStockScheduler.class);

    @Scheduled(cron = "0 */1 * * * *")   // every 1 minute, for easy testing
    public void checkLowStock() {
        List<Product> lowStockProducts = productRepository.findByStockQuantityLessThan(10);

        if (lowStockProducts.isEmpty()) {
            log.info("Low stock check: all products have sufficient stock.");
        } else {
            lowStockProducts.forEach(p ->
                    log.warn("LOW STOCK ALERT: '{}' has only {} units left", p.getName(), p.getStockQuantity())
            );
        }
    }
}