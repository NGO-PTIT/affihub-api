package com.affihub.endpoints;

import com.affihub.object.Category;
import com.affihub.service.CategoryService;
import com.affihub.service.implement.CategoryServiceImplement;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
public class CategoryEndpoint {
    private static final CategoryService categoryService = new CategoryServiceImplement();

    @GET
    public Response getCategories(@QueryParam("parentId") Long parentId) {
        List<Category> result = parentId == null
                ? categoryService.findAll()
                : categoryService.findByParentId(parentId);

        JSONArray data = new JSONArray();
        for (Category category : result) {
            data.put(category.getAsJSONObject());
        }

        return Response.ok(new JSONObject()
                        .put("data", data)
                        .put("total", result.size())
                        .toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
