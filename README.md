# AffiHub API

Base project REST API bằng Jakarta REST / JAX-RS, triển khai bằng Jersey. Dự án không dùng Spring Boot controller.

## Yêu cầu

- Java 11+
- Maven 3.9+

## Chạy API

```bash
mvn clean compile
mvn exec:java
```

Server chạy tại:

```text
http://localhost:8080/api
```

## Deploy lên Render

1. Đẩy thư mục này lên một GitHub repository.
2. Trong Render, chọn **New > Blueprint** và chọn repository. Render sẽ đọc `render.yaml`.
3. Nhập các biến môi trường của PostgreSQL/Neon: `POSTGRES_HOST`, `POSTGRES_USER`,
   `POSTGRES_PASSWORD`, và `POSTGRES_DATABASE`. `POSTGRES_HOST` phải gồm cả cổng, ví dụ
   `host:5432`.
4. Sau khi deploy, API có địa chỉ `https://<ten-dich-vu>.onrender.com/api/products`.

Render tự cung cấp biến `PORT`; ứng dụng sử dụng biến này khi chạy production. Blueprint chạy
qua Docker vì Render không hỗ trợ Java như một native runtime trong `render.yaml`.

## Product endpoints

```text
GET    /api/products       Danh sách sản phẩm
GET    /api/products/{id}  Chi tiết sản phẩm
POST   /api/products       Thêm sản phẩm
PUT    /api/products/{id}  Sửa sản phẩm
DELETE /api/products/{id}  Xóa sản phẩm
```

Khi tạo sản phẩm, `category` phải là một trong các giá trị: `Electronics`, `Fashion`,
`Home & Living`, `Beauty`, `Health`, `Sports`, `Books`, `Food & Beverage`, `Pets`, `Kids`, hoặc `Other`.

## Ví dụ request

Thêm sản phẩm:

```bash
curl -X POST http://localhost:8080/api/products \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "Laptop",
    "description": "Laptop văn phòng",
    "category": "Electronics",
    "price": 1200000,
    "clickSum": 0,
    "imageUrl": "https://example.com/laptop.jpg",
    "affiliateLink": "https://example.com/laptop"
  }'
```

Sửa sản phẩm:

```bash
curl -X PUT http://localhost:8080/api/products/1 \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "Laptop Pro",
    "description": "Laptop văn phòng cấu hình cao",
    "category": "Electronics",
    "price": 1500000,
    "clickSum": 0,
    "imageUrl": "https://example.com/laptop-pro.jpg",
    "affiliateLink": "https://example.com/laptop-pro"
  }'
```

Xóa sản phẩm:

```bash
curl -X DELETE http://localhost:8080/api/products/1
```

## Ghi chú kỹ thuật

- `endpoints/ProductEndpoint` là JAX-RS endpoint xử lý HTTP API.
- `object/Product` chứa object dùng cho request/response.
- `service/ProductService` định nghĩa nghiệp vụ sản phẩm.
- `service/implement/ProductServiceImplement` triển khai nghiệp vụ.
- `service/implement/InMemoryProductStore` hiện lưu dữ liệu in-memory để dự án chạy ngay.
- `service/mapping/ProductMapper` chuẩn hóa dữ liệu sản phẩm trước khi lưu.
- `service/utility/ProductValidator` validate dữ liệu đầu vào.
- Khi cần dùng database, thay `InMemoryProductStore` bằng tầng persistence dùng JDBC/JPA.
# affihub-api
