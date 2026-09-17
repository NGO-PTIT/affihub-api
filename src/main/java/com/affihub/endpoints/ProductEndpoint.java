package com.affihub.endpoints;

import com.affihub.object.Category;
import com.affihub.object.Product;
import com.affihub.object.ProductClick;
import com.affihub.service.CategoryService;
import com.affihub.service.ProductClickService;
import com.affihub.service.ProductService;
import com.affihub.service.implement.CategoryServiceImplement;
import com.affihub.service.implement.ProductClickServiceImplement;
import com.affihub.service.implement.ProductServiceImplement;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductEndpoint {
    private static final ProductService productService = new ProductServiceImplement();
    private static final ProductClickService productClickService = new ProductClickServiceImplement();
    private static final CategoryService categoryService = new CategoryServiceImplement();

    @GET
    public Response getProducts(@QueryParam("categoryId") Long categoryId) throws Exception {
        List<Product> products = categoryId == null
                ? productService.findAll()
                : productService.findByCategoryId(categoryId);

        JSONArray data = new JSONArray();
        for (Product product : products) {
            data.put(product.getAsJSONObject());
        }

        JSONObject response = new JSONObject()
                .put("data", data)
                .put("total", products.size());

        return Response
                .ok(response.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("/{id}")
    public Response getProduct(@PathParam("id") Long id) throws Exception {
        Product product = productService.findById(id);
        if (product == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response
                .ok(product.getAsJSONObject().toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @POST
    public Response createProduct(String body) throws Exception {
        JSONObject jsonObject = new JSONObject(body);
        long timeNow = System.currentTimeMillis();

        long categoryId =  jsonObject.optLong("categoryId");
        if (categoryId == 0) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            Category category = categoryService.findById(categoryId);
            if (category == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        }
        Product product = new Product(
                timeNow,
                jsonObject.optString("name"),
                jsonObject.optString("description"),
                categoryId,
                jsonObject.optLong("price"),
                0,
                jsonObject.optString("image_url"),
                jsonObject.optString("affiliate_link"),
                timeNow,
                timeNow
        );
        productService.create(product);

        return Response
                .status(Response.Status.CREATED)
                .entity(product.getAsJSONObject().toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @POST
    @Path("/{id}/clicks")
    public Response createProductClick(@PathParam("id") long productId, String body) throws Exception {
        productService.findById(productId);

        JSONObject request = new JSONObject(body);
        JSONObject sourceInfo = request.optJSONObject("source_info");
        if (sourceInfo == null) {
            sourceInfo = new JSONObject();
        }

        Product product = productService.findById(productId);
        if (product == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        ProductClick productClick = new ProductClick(productId, System.currentTimeMillis(), sourceInfo);
        productClickService.create(productClick);

        product.setClickSum(product.getClickSum() + 1);
        productService.update(productId, product);

        return Response
                .status(Response.Status.CREATED)
                .entity(productClick.getAsJSONObject().toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("/{id}/clicks")
    public Response countProductClicks(@PathParam("id") long productId,
                                       @QueryParam("from") Long from,
                                       @QueryParam("to") Long to) throws Exception {
        productService.findById(productId);

        if (from == null || to == null || from >= to) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(new JSONObject()
                            .put("error", "from and to are required, and from must be less than to")
                            .toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        long totalClicks = productClickService.countByProductAndTime(productId, from, to);
        JSONObject response = new JSONObject()
                .put("productId", productId)
                .put("from", from)
                .put("to", to)
                .put("totalClicks", totalClicks);

        return Response
                .ok(response.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response updateProduct(@PathParam("id") long id, String body) throws Exception {
        Product existing = productService.findById(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        JSONObject jsonObject = new JSONObject(body);
        if (jsonObject.has("name")) {
            existing.setName(jsonObject.getString("name"));
        }
        if (jsonObject.has("description")) {
            existing.setDescription(jsonObject.getString("description"));
        }
        if (jsonObject.has("categoryId")) {
            existing.setCategoryId(jsonObject.getLong("categoryId"));
        }
        if (jsonObject.has("price")) {
            existing.setPrice(jsonObject.getLong("price"));
        }
        if (jsonObject.has("imageUrl")) {
            existing.setImageUrl(jsonObject.getString("imageUrl"));
        }
        if (jsonObject.has("affiliateLink")) {
            existing.setAffiliateLink(jsonObject.getString("affiliateLink"));
        }
        existing.setUpdatedTime(System.currentTimeMillis());
        productService.update(id, existing);
        return Response
                .status(Response.Status.OK)
                .entity(existing.getAsJSONObject().toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteProduct(@PathParam("id") long id) {
        productService.delete(id);
        return Response.ok().build();
    }

}
