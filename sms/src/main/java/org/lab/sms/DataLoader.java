package org.lab.sms;

import org.lab.sms.dataaccess.StockRepository;
import org.lab.sms.domain.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements ApplicationRunner {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    StockRepository stockRepository;

    public void run(ApplicationArguments args) {
        System.out.println("Clearing tables...");
        jdbcTemplate.update("delete from item");
        jdbcTemplate.update("delete from location");
        jdbcTemplate.update("delete from product");

        stockRepository.insert(new Location("S0", "Store 0"));
        stockRepository.insert(new Location("S1", "Store 1"));
        stockRepository.insert(new Location("S2", "Store 2"));
        stockRepository.insert(new Location("S3", "Store 3"));
        stockRepository.insert(new Location("W0", "Warehouse 0"));
        stockRepository.insert(new Location("W1", "Warehouse 1"));
        stockRepository.insert(new Location("W2", "Warehouse 2"));
        stockRepository.insert(new Location("W3", "Warehouse 3"));
    }
}
