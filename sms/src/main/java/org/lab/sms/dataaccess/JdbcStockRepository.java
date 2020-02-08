package org.lab.sms.dataaccess;

import org.lab.sms.domain.Item;
import org.lab.sms.domain.Location;
import org.lab.sms.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcStockRepository implements StockRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int insert(Location location) throws DataAccessException {
        return jdbcTemplate.update(
                "insert into location (code, name) values(?,?)",
                location.getCode(), location.getName());
    }

    @Override
    public int update(Location location) throws DataAccessException {
        return jdbcTemplate.update(
                "update location set name = ? where code = ?",
                location.getName(), location.getCode());

    }

    @Override
    public void save(Location location) throws DataAccessException {
        if (update(location) == 0) insert(location);
    }

    @Override
    public Location findLocation(String code) throws DataAccessException {
        try {
            return jdbcTemplate.queryForObject(
                    "select * from location where code = ?",
                    new Object[]{code},
                    (rs, rowNum) ->
                            new Location(
                                    rs.getString("code"),
                                    rs.getString("name")
                            )
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Location> findAllLocations() throws DataAccessException {
        List<Location> locations = jdbcTemplate.query(
                "select * from location order by code",
                (rs, rowNum) ->
                        new Location(
                                rs.getString("code"),
                                rs.getString("name")
                        )
        );
        if (locations == null) locations = new ArrayList<>();
        return locations;
    }

    @Override
    public int insert(Product product) throws DataAccessException {
        return jdbcTemplate.update(
                "insert into product (id, name) values(?,?)",
                product.getId(), product.getName());
    }

    @Override
    public int update(Product product) throws DataAccessException {
        return jdbcTemplate.update(
                "update product set name = ? where id = ?",
                product.getName(), product.getId());
    }

    @Override
    public void save(Product product) throws DataAccessException {
        if (update(product) == 0) insert(product);
    }

    @Override
    public Product findProduct(String id) throws DataAccessException {
        try {
            return jdbcTemplate.queryForObject(
                    "select * from product where id = ?",
                    new Object[]{id},
                    (rs, rowNum) ->
                            new Product(
                                    rs.getString("id"),
                                    rs.getString("name")
                            )
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Product> findAllProducts() throws DataAccessException {
        List<Product> products = jdbcTemplate.query(
                "select * from product order by id",
                (rs, rowNum) ->
                        new Product(
                                rs.getString("id"),
                                rs.getString("name")
                        )
        );
        if (products == null) products = new ArrayList<>();
        return products;
    }

    @Override
    public int insert(Item item) throws DataAccessException {
        return jdbcTemplate.update(
                "insert into item (product_id, location_code, quantity) values(?,?,?)",
                item.getProductId(), item.getLocationCode(), item.getQuantity());
    }

    @Override
    public int update(Item item) throws DataAccessException {
        return jdbcTemplate.update(
                "update item set quantity = ? where product_id = ? and location_code = ?",
                item.getQuantity(), item.getProductId(), item.getLocationCode());
    }

    @Override
    public void save(Item item) throws DataAccessException {
        if (update(item) == 0) insert(item);
    }

    @Override
    public Item findItem(String productId, String locationCode) throws DataAccessException {
        try {
            return jdbcTemplate.queryForObject(
                    "select * from item where product_id = ? and location_code = ?",
                    new Object[]{productId, locationCode},
                    (rs, rowNum) ->
                            new Item(
                                    rs.getString("product_id"),
                                    rs.getString("location_code"),
                                    rs.getInt("quantity")
                            )
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Item> findItems(List<String> productIds) throws DataAccessException {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate.getDataSource());
        SqlParameterSource namedParameters = new MapSqlParameterSource("productIds", productIds);
        List<Item> items = namedParameterJdbcTemplate.query(
                "select * from item where product_id in (:productIds) order by product_id, location_code ",
                namedParameters,
                (rs, rowNum) ->
                        new Item(
                                rs.getString("product_id"),
                                rs.getString("location_code"),
                                rs.getInt("quantity")
                        )
        );
        if (items == null) items = new ArrayList<>();
        return items;
    }

    @Override
    public List<Item> findAllItems() throws DataAccessException {
        List<Item> items = jdbcTemplate.query(
                "select * from item order by product_id, location_code",
                (rs, rowNum) ->
                        new Item(
                                rs.getString("product_id"),
                                rs.getString("location_code"),
                                rs.getInt("quantity")
                        )
        );
        if (items == null) items = new ArrayList<>();
        return items;
    }

    @Override
    public int addQuantity(Item item) throws DataAccessException {
        if (jdbcTemplate.update(
                "update item set quantity = quantity + ? where product_id = ? and location_code = ?",
                item.getQuantity(), item.getProductId(), item.getLocationCode()) == 1) {
            return 1;
        } else {
            return insert(item);
        }
    }
}
