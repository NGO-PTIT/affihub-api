package com.affihub.service.implement;

import com.affihub.config.MicroServiceConfig;
import com.affihub.config.PostgresConfig;
import com.affihub.lib.db.PostgresCrudRepository;
import com.affihub.object.ProductClick;
import com.affihub.service.ProductClickService;

public class ProductClickServiceImplement implements ProductClickService {
    private static final PostgresConfig POSTGRES_CONFIG = MicroServiceConfig.load().getPostgres();
    private static final PostgresCrudRepository POSTGRES_REPOSITORY =
            new PostgresCrudRepository(POSTGRES_CONFIG);

    @Override
    public void create(ProductClick productClick) {
        if (POSTGRES_CONFIG.isReadOnly()) {
            throw new IllegalStateException("Postgres config is readOnly");
        }

        POSTGRES_REPOSITORY.executeQuery(
                INSERT_PRODUCT_CLICK,
                productClick.getProductId(),
                productClick.getClickedTime(),
                productClick.getSourceInfo().toString()
        );
    }

    @Override
    public long countByProductAndTime(long productId, long from, long to) {
        Object value = POSTGRES_REPOSITORY.fetch(
                        COUNT_PRODUCT_CLICKS,
                        productId,
                        from,
                        to
                )
                .get(0)
                .get("click_count");

        return value instanceof Number ? ((Number) value).longValue() : Long.parseLong(String.valueOf(value));
    }

    public static void shutdown() {
        POSTGRES_REPOSITORY.close();
    }

    private static final String INSERT_PRODUCT_CLICK =
            "INSERT INTO ProductClick (productId, clickedTime, sourceInfo) " +
                    "VALUES (?, ?, ?::jsonb)";

    private static final String COUNT_PRODUCT_CLICKS =
            "SELECT COUNT(*) AS click_count " +
                    "FROM ProductClick " +
                    "WHERE productId = ? AND clickedTime >= ? AND clickedTime < ?";
}
