package com.mockcompany.webapp.service;

import com.mockcompany.webapp.data.ProductItemRepository;
import com.mockcompany.webapp.model.ProductItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * This class will create random products from a list of images
 */
@Service
public class SearchService {

    final ProductItemRepository productItemRepository;

    @Autowired
    public SearchService(ProductItemRepository productItemRepository) {
        this.productItemRepository = productItemRepository;
    }

    /**
     * The search method, annotated with @GetMapping telling spring this method should be called
     * when an HTTP GET on the path /api/products/search is made.  A single query parameter is declared
     * using the @RequestParam annotation.  The value that is passed when performing a query will be
     * in the query parameter.
     * @param query
     * @return The filtered products
     */
    public Collection<ProductItem> search(String query) {
        Iterable<ProductItem> allItems = this.productItemRepository.findAll();
        List<ProductItem> itemList = new ArrayList<>();

        boolean exactMatch = false;
        if (query.startsWith("\"") && query.endsWith("\"")) {
            exactMatch = true;
            // Problem here arises if we don't remove the quotes from the query
            query = query.substring(1, query.length() - 1);
        }

        // If we are not doing an exact match
        // Set the query to lower case so that our query matching can ignore any casing
        query = query.toLowerCase();

        // This is a loop that the code inside will execute on each of the items from the database.
        for (ProductItem item : allItems) {
            boolean nameMatch;
            boolean descriptionMatch;

            // Choose the kind of matching we are doing
            if (exactMatch) {
                nameMatch = item.getName().toLowerCase().matches(query);
                descriptionMatch = item.getDescription().toLowerCase().matches(query);
            } else {
                nameMatch = item.getName().toLowerCase().contains(query);
                descriptionMatch = item.getDescription().toLowerCase().contains(query);
            }

            if (nameMatch || descriptionMatch) {
                itemList.add(item);
            }
        }
        return itemList;
    }
}
