package org.lab.sms;

import org.junit.jupiter.api.*;
import org.lab.sms.dataaccess.StockRepository;
import org.lab.sms.domain.Location;
import org.lab.sms.domain.Product;
import org.lab.sms.logic.Stock;
import org.lab.sms.logic.StockManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LogicTests {

    @Autowired
    StockManager stockManager;

    @Autowired
    StockRepository stockRepository;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    @Order(0)
    void contextLoads() {
        System.out.println("Clearing tables...");
        jdbcTemplate.update("delete from item");
        jdbcTemplate.update("delete from location");
        jdbcTemplate.update("delete from product");

        stockRepository.insert(new Location("S0", "Store 0"));
        stockRepository.insert(new Location("S1", "Store 1"));
        stockRepository.insert(new Location("S2", "Store 2"));
        stockRepository.insert(new Location("S3", "Store 3"));

    }

    @Test
    @Order(1)
    void settingStockLevels() {

        List<Product> products = new ArrayList<>();
        products.add(new Product("A-0", "Part A-0"));
        products.add(new Product("A-1", "Part A-1"));
        products.add(new Product("A-2", "Part A-2"));
        products.add(new Product("A-3", "Part A-3"));

        List<Stock> stocks = new ArrayList<>();
        for (int store = 0; store <= 3; store++) {
            for (int part = 0; part <= 3; part++) {
                stocks.add(new Stock(products.get(part).getId(),
                        products.get(part).getName(),
                        "S" + store, 10 * store));
            }
        }

        try {
            stockManager.setStockLevels(stocks);
        } catch (StockingException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    void changeStockLevels() {

        List<Product> products = new ArrayList<>();
        products.add(new Product("A-0", "Part A-0"));
        products.add(new Product("A-1", "Part A-1"));
        products.add(new Product("A-2", "Part A-2"));
        products.add(new Product("A-3", "Part A-3"));

        List<Stock> stocks = new ArrayList<>();
        for (int store = 0; store <= 3; store++) {
            for (int part = 0; part <= 3; part++) {
                stocks.add(new Stock(products.get(part).getId(),
                        products.get(part).getName(),
                        "S" + store, store));
            }
        }

        try {
            stockManager.updateStockLevels(stocks);
        } catch (StockingException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    void getStockLevels() {
        List<Stock> stocks = stockManager.getStockLevels();
        for (Stock stock : stocks) {
            System.out.println(stock.getProductId()+","+stock.getProductName()+","+stock.getLocationCode()+
                    ","+stock.getQuantity());
        }
    }


}
