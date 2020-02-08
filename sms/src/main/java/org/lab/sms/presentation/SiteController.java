package org.lab.sms.presentation;

import org.lab.sms.logic.Stock;
import org.lab.sms.logic.StockManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RestController
public class SiteController {

    @Autowired
    StockManager stockManager;

    @RequestMapping("/stocklevels")
    public String index() {
        List<Stock> stocks = stockManager.getStockLevels();
        StringBuffer buf = new StringBuffer();
        buf.append("<ol>");
        for (Stock stock : stocks) {
            buf.append("<li>"+stock.getProductId()+","+stock.getProductName()+","+stock.getLocationCode()+","+stock.getQuantity());
        }
        return "<h1>SMS Product Stocking Levels</h1>"+buf.toString();
    }

}