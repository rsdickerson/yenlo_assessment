package org.lab.sms.dataaccess;

import javafx.collections.transformation.SortedList;
import org.lab.sms.domain.Item;
import org.lab.sms.domain.Location;
import org.lab.sms.domain.Product;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository {

    public int insert(Location location) throws DataAccessException;
    public int update(Location location) throws DataAccessException;
    public void save(Location location) throws DataAccessException;
    public Location findLocation(String code) throws DataAccessException;
    public List<Location> findAllLocations() throws DataAccessException;

    public int insert(Product product) throws DataAccessException;
    public int update(Product product) throws DataAccessException;
    public void save(Product product) throws DataAccessException;
    public Product findProduct(String id) throws DataAccessException;
    public List<Product> findAllProducts() throws DataAccessException;

    public int insert(Item item) throws DataAccessException;
    public int update(Item item) throws DataAccessException;
    public void save(Item item) throws DataAccessException;
    public int addQuantity(Item item) throws DataAccessException;
    public Item findItem(String productId, String locationCode) throws DataAccessException;
    public List<Item> findItems(List<String> productIds) throws DataAccessException;
    public List<Item> findAllItems() throws DataAccessException;

}
