package com.affihub.service.implement;

import com.affihub.config.MicroServiceConfig;
import com.affihub.config.PostgresConfig;
import com.affihub.lib.db.PostgresCrudRepository;
import com.affihub.object.Product;
import com.affihub.service.ProductService;
import com.affihub.service.mapping.ProductMapper;
import jakarta.ws.rs.NotFoundException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductServiceImplement implements ProductService {
    private static final PostgresConfig POSTGRES_CONFIG = MicroServiceConfig.load().getPostgres();
    private static final PostgresCrudRepository POSTGRES_REPOSITORY = new PostgresCrudRepository(POSTGRES_CONFIG);

    private final ProductMapper mapper = new ProductMapper();

    public ProductServiceImplement() {
    }

    public static void initialize() {
        // Force static initialization at application startup.
    }

    public static void shutdown() {
        POSTGRES_REPOSITORY.close();
    }

    @Override
    public List<Product> findAll() {
        return POSTGRES_REPOSITORY.fetch(FIND_PRODUCTS).stream()
                .map(mapper::mapRow)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByCategoryId(long categoryId) {
        return POSTGRES_REPOSITORY.fetch(FIND_PRODUCTS_BY_CATEGORY_ID, categoryId).stream()
                .map(mapper::mapRow)
                .collect(Collectors.toList());
    }

    @Override
    public Product findById(Long id) {
        Map<String, Object> row = POSTGRES_REPOSITORY.findByPrimaryKeys(FIND_PRODUCT_BY_ID, id);
        if (row == null) {
            throw new NotFoundException("Product not found: " + id);
        }
        return mapper.mapRow(row);
    }

    @Override
    public void create(Product product) {
        assertWritable();
        POSTGRES_REPOSITORY.executeQuery(
                INSERT_PRODUCT,
                product.getProductId(),
                product.getName(),
                product.getDescription(),
                product.getCategoryId(),
                product.getPrice(),
                product.getClickSum(),
                product.getImageUrl(),
                product.getAffiliateLink(),
                product.getCreatedTime(),
                product.getUpdatedTime()
        );
    }

    @Override
    public void update(Long id, Product product) {
        assertWritable();
        findById(id);
        POSTGRES_REPOSITORY.executeQuery(
                UPDATE_PRODUCT,
                product.getName(),
                product.getDescription(),
                product.getCategoryId(),
                product.getPrice(),
                product.getClickSum(),
                product.getImageUrl(),
                product.getAffiliateLink(),
                product.getCreatedTime(),
                product.getUpdatedTime(),
                id
        );
    }

    @Override
    public void delete(Long id) {
        assertWritable();
        findById(id);
        POSTGRES_REPOSITORY.executeQuery(DELETE_PRODUCT, id);
    }

    private void assertWritable() {
        if (POSTGRES_CONFIG.isReadOnly()) {
            throw new IllegalStateException("Postgres config is readOnly");
        }
    }

    private Map<String, Object> firstRow(List<Map<String, Object>> rows) {
        if (rows == null || rows.isEmpty()) {
            throw new IllegalStateException("Database did not return product row");
        }
        return rows.get(0);
    }

    private static final String INSERT_PRODUCT =
            "INSERT INTO product " +
                    "(productid, name, description, categoryid, price, clicksum, imageurl, affiliatelink, " +
                    "createdtime, updatedtime) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                    "ON CONFLICT (productid) " +
                    "DO UPDATE SET " +
                    "name = EXCLUDED.name, " +
                    "description = EXCLUDED.description, " +
                    "categoryid = EXCLUDED.categoryid, " +
                    "price = EXCLUDED.price, " +
                    "clicksum = EXCLUDED.clicksum, " +
                    "imageurl = EXCLUDED.imageurl, " +
                    "affiliatelink = EXCLUDED.affiliatelink, " +
                    "createdtime = EXCLUDED.createdtime, " +
                    "updatedtime = EXCLUDED.updatedtime;";

    private static final String UPDATE_PRODUCT =
            "UPDATE product " +
                    "SET name = ?, description = ?, categoryid = ?, price = ?, clicksum = ?, " +
                    "imageurl = ?, affiliatelink = ?, createdtime = ?, updatedtime = ? " +
                    "WHERE productid = ?";

    private static final String DELETE_PRODUCT =
            "DELETE FROM product WHERE productid = ?";

    private static final String FIND_PRODUCT_BY_ID =
            "SELECT * FROM product WHERE productid = ?";

    private static final String FIND_PRODUCTS =
            "SELECT * FROM product ORDER BY productid";

    private static final String FIND_PRODUCTS_BY_CATEGORY_ID =
            "SELECT * FROM product WHERE categoryid = ? ORDER BY productid";

}
