package org.lab.sms;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.lab.sms.dataaccess.StockRepository;
import org.lab.sms.domain.Item;
import org.lab.sms.domain.Location;
import org.lab.sms.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DataAccessTests {

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
    }

    @Test
    @Order(1)
    void saveLocation() {
        Location location = new Location("S1", "Store 1");
        assertEquals(1, stockRepository.insert(location));
        Location location1 = stockRepository.findLocation("S1");
        assertNotEquals(null, location1);
        assertTrue(location.equals(location1));
        Location location2 = new Location("S2","Store 2");
        assertEquals(1, stockRepository.insert(location2));
        assertEquals(2,stockRepository.findAllLocations().size());
    }

    @Test
    @Order(2)
    void saveLocationFail() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> saveLocation()); // should fail
    }

    @Test
    @Order(3)
    void saveProduct() {
        Product product = new Product("GH-100", "Garden Hose 100'");
        assertEquals(1, stockRepository.insert(product));
        Product product1 = stockRepository.findProduct("GH-100");
        assertNotEquals(null, product1);
        assertTrue(product.equals(product1));
        Product product2 = new Product("MB-2231","Mailbox model 2231");
        assertEquals(1, stockRepository.insert(product2));
        assertEquals(2,stockRepository.findAllProducts().size());
    }

    @Test
    @Order(4)
    void saveProductFail() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> saveProduct()); // should fail
    }

    @Test
    @Order(5)
    void saveItem() {
        Item item = new Item("GH-100", "S1",10);
        assertEquals(1, stockRepository.insert(item));
        item.setQuantity(100);
        assertEquals(1, stockRepository.update(item));
        Item item1 = stockRepository.findItem("GH-100", "S1");
        assertNotEquals(null, item1);
        assertTrue(item1.equals(item));
        Item item2 = new Item("MB-2231", "S2", 20);
        assertEquals(1, stockRepository.insert(item2));
        assertEquals(2,stockRepository.findAllItems().size());
        List<String> productsIds = new ArrayList<>();
        productsIds.add("GH-100");
        productsIds.add("MB-2231");
        assertEquals(2,stockRepository.findItems(productsIds).size());
    }

    @Test
    @Order(6)
    void saveItemFail() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> saveItem()); // should fail
        Item item = new Item("XX-000","W10",100);
        assertThrows(DataAccessException.class, () -> stockRepository.save(item)); // should fail, prod and location do not exist
    }


}
