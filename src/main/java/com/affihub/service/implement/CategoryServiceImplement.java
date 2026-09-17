package com.affihub.service.implement;

import com.affihub.config.MicroServiceConfig;
import com.affihub.config.PostgresConfig;
import com.affihub.lib.db.PostgresCrudRepository;
import com.affihub.object.Category;
import com.affihub.service.CategoryService;
import com.affihub.service.mapping.CategoryMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CategoryServiceImplement implements CategoryService {
    private static final PostgresConfig POSTGRES_CONFIG = MicroServiceConfig.load().getPostgres();
    private static final PostgresCrudRepository POSTGRES_REPOSITORY = new PostgresCrudRepository(POSTGRES_CONFIG);

    private final CategoryMapper mapper = new CategoryMapper();

    public static void shutdown() {
        POSTGRES_REPOSITORY.close();
    }

    @Override
    public List<Category> findAll() {
        return POSTGRES_REPOSITORY.fetch(FIND_CATEGORIES).stream()
                .map(mapper::mapRow)
                .collect(Collectors.toList());
    }

    @Override
    public List<Category> findByParentId(long parentId) {
        return POSTGRES_REPOSITORY.fetch(FIND_CATEGORIES_BY_PARENT_ID, parentId).stream()
                .map(mapper::mapRow)
                .collect(Collectors.toList());
    }

    @Override
    public Category findById(long id) {
        Map<String, Object> row = POSTGRES_REPOSITORY.findByPrimaryKeys(FIND_CATEGORIES_BY_ID, id);
        return mapper.mapRow(row);
    }

    private static final String FIND_CATEGORIES =
            "SELECT * FROM category ORDER BY parentId, categoryId";

    private static final String FIND_CATEGORIES_BY_PARENT_ID =
            "SELECT * FROM category WHERE parentId = ? ORDER BY categoryId";

    private static final String FIND_CATEGORIES_BY_ID =
            "SELECT * FROM category WHERE categoryId = ?";
}
