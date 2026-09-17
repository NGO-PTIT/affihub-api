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
    public List<Product> findByCategory(String category) {
        return POSTGRES_REPOSITORY.fetch(FIND_PRODUCTS_BY_CATEGORY, category).stream()
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
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getCategory(),
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
                product.getCategory(),
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
                    "(id, name, description, category, price, clickSum, imageUrl, affiliateLink, " +
                    "createdTime, updatedTime) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                    "ON CONFLICT (id) " +
                    "DO UPDATE SET " +
                    "name = EXCLUDED.name, " +
                    "description = EXCLUDED.description, " +
                    "category = EXCLUDED.category, " +
                    "price = EXCLUDED.price, " +
                    "clickSum = EXCLUDED.clickSum, " +
                    "imageUrl = EXCLUDED.imageUrl, " +
                    "affiliateLink = EXCLUDED.affiliateLink, " +
                    "createdTime = EXCLUDED.createdTime, " +
                    "updatedTime = EXCLUDED.updatedTime;";

    private static final String UPDATE_PRODUCT =
            "UPDATE products " +
                    "SET name = ?, description = ?, category = ?, price = ?, clickSum = ?, " +
                    "imageUrl = ?, affiliateLink = ?, createdTime = ?, updatedTime = ? " +
                    "WHERE id = ?";

    private static final String DELETE_PRODUCT =
            "DELETE FROM products WHERE id = ?";

    private static final String FIND_PRODUCT_BY_ID =
            "SELECT * FROM products WHERE id = ?";

    private static final String FIND_PRODUCTS =
            "SELECT * FROM products ORDER BY id";

    private static final String FIND_PRODUCTS_BY_CATEGORY =
            "SELECT * FROM products WHERE category = ? ORDER BY id";

}
