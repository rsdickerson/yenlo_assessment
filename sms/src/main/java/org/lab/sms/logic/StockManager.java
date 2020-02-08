package org.lab.sms.logic;

import org.lab.sms.StockingException;
import org.lab.sms.dataaccess.StockRepository;
import org.lab.sms.domain.Item;
import org.lab.sms.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@Component
public class StockManager {

    @Autowired
    StockRepository stockRepository;

    public StockManager() {
    }

    @Transactional
    public void setStockLevels(List<Stock> stockLevels) throws StockingException {
        try {
            Map<String, String> productMap = getProductMap();
            for (Stock stock : stockLevels) {
                if (!productMap.containsKey(stock.getProductId())) {
                    if (stock.getProductName() == null) {
                        throw new StockingException("ProductID [" + stock.getProductId() + "] is missing product name value");
                    }
                    Product product = new Product(stock.getProductId(), stock.getProductName());
                    stockRepository.save(product);
                }
                Item item = new Item(stock.getProductId(), stock.getLocationCode(), stock.getQuantity());
                stockRepository.save(item);
            }
        } catch (DataAccessException e) {
            throw new StockingException(e.getCause().getMessage());
        }
    }

    @Transactional
    public void updateStockLevels(List<Stock> stockLevels) throws StockingException {
        try {
            Map<String, String> productMap = getProductMap();
            for (Stock stock : stockLevels) {
                if (!productMap.containsKey(stock.getProductId())) {
                    throw new StockingException("ProductID [" + stock.getProductId() + "] is invalid");
                }

                Item item = new Item(stock.getProductId(), stock.getLocationCode(), stock.getQuantity());
                stockRepository.addQuantity(item);
            }
        } catch (DataAccessException e) {
            throw new StockingException(e.getCause().getMessage());
        }
    }

    /**
     * Read lines and update stock level
     * @param inputStream of lines : productId,change,location
     */
    @Transactional
    public int updateStockLevels(InputStream inputStream) {
        //BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream),1024*1024);
        try {
            int lineCount = 0;
            Scanner sc = new Scanner(inputStream, "UTF-8");
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line == null || line.length() == 0) continue;
                lineCount++;
                if (lineCount == 591) {
                    System.out.println("This is the line");
                }
                String[] values = line.split("\\s?,\\s?");
                if (values.length != 3) {
                    throw new StockingException("[Line#" + lineCount + "] line formatting is invalid [" + line + "]");
                }
                try {
                    Item item = new Item(values[0], values[2], Integer.parseInt(values[1]));
                    stockRepository.addQuantity(item);
                } catch (NumberFormatException nfe) {
                    throw new StockingException("[Line#" + lineCount + "] quantity [" + values[2] + "] is invalid [" + line + "]");
                }
            }
            return lineCount;
        } catch (DataAccessException e) {
            throw new StockingException(e.getCause().getMessage());
        //} catch (IOException e) {
          //  throw new StockingException("Problem with input stream; " + e.getMessage());
        }
    }

    public List<Stock> getStockLevels() {

        Map<String, String> productMap = getProductMap();
        List<Item> items = stockRepository.findAllItems();
        ArrayList<Stock> stocks = new ArrayList<>();
        for (Item item : items) {
            String productName = productMap.containsKey(item.getProductId()) ?
                    productMap.get(item.getProductId()) : "missing";
            stocks.add(new Stock(item.getProductId(), productName, item.getLocationCode(), item.getQuantity()));
        }
        return stocks;

    }

    public List<Stock> getStockLevels(List<String> productIds) {
        Map<String, String> productMap = getProductMap();
        List<Item> items = stockRepository.findItems(productIds);
        ArrayList<Stock> stocks = new ArrayList<>();
        for (Item item : items) {
            String productName = productMap.containsKey(item.getProductId()) ?
                    productMap.get(item.getProductId()) : "missing";
            stocks.add(new Stock(item.getProductId(), productName, item.getLocationCode(), item.getQuantity()));
        }
        return stocks;
    }

    private Map<String, String> getProductMap() {
        HashMap<String, String> productMap = new HashMap<>();
        List<Product> products = stockRepository.findAllProducts();
        for (Product product : products) {
            productMap.put(product.getId(), product.getName());
        }
        return productMap;
    }

}
