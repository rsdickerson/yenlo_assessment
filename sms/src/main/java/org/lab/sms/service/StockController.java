package org.lab.sms.service;

import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.lab.sms.logic.Stock;
import org.lab.sms.logic.StockManager;
import org.lab.sms.StockingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@Validated
public class StockController {

    @Autowired
    StockManager stockManager;

    public StockController() {
    }

    @PostMapping("/stock/level")
    @ResponseStatus(HttpStatus.OK)
    String setStockLevels(@RequestBody List<Stock> stocks) throws StockingException {
        stockManager.setStockLevels(stocks);
        return "Success";
    }

    @PatchMapping("/stock/level")
    @ResponseStatus(HttpStatus.OK)
    String updateStockLevel(@RequestBody List<Stock> stocks) throws StockingException {
        stockManager.updateStockLevels(stocks);
        return "Success";
    }

    @GetMapping("/stock/level")
    @ResponseStatus(HttpStatus.OK)
    List<Stock> getStockLevel() throws StockingException {
        return stockManager.getStockLevels();
    }

    @GetMapping(value = "/stock/level", params = "ids")
    @ResponseStatus(HttpStatus.OK)
    List<Stock> getStockLevel(@RequestParam List<String> ids) throws StockingException {
        return stockManager.getStockLevels(ids);
    }

    @PostMapping("/stock/level/file-upload")
    public String handleFileUpload(final HttpServletRequest request) throws FileUploadException, IOException {

        boolean isMultipart = ServletFileUpload.isMultipartContent(request);

        if (!isMultipart) {
            // consider raising an error here if desired
        }

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload();

        FileItemIterator iter;
        InputStream fileStream = null;
        String name = null;

        // retrieve the multi-part constituent items parsed from the request
        iter = upload.getItemIterator(request);

        // loop through each item
        while (iter.hasNext()) {
            FileItemStream item = iter.next();
            name = item.getName();
            fileStream = item.openStream();

            // check if the item is a file
            if (!item.isFormField()) {
                System.out.println("Receiving file " + item.getName());
                break; // break here so that the input stream can be processed
            }
        }

        if (fileStream != null) {
            return "Success with " + stockManager.updateStockLevels(fileStream) + " lines processed";
        } else {
            return "No file uploaded to process";
        }
    }
}
