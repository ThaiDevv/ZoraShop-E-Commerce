# Nợ kỹ thuật (Tech Debt): ZoraShop

> Nguồn: review Bước 1–2 của task Refresh Token (whitelist + rotation), ngày 2026-10-02.
> Mỗi mục ghi **vấn đề, vì sao, hướng sửa**. Chỉ là gợi ý, code thì tự viết.

## Tổng quan

| ID | Hạng mục | Ưu tiên | Trạng thái |
|---|---|---|---|
| RT-0 | Migration V13 `refresh_token` | Cao | ✅ Đã sửa |
| RT-1 | `RefreshToken.users` → đổi tên `user` | Thấp | ✅ Đã sửa |
| RT-2 | `RefreshToken` thiếu `nullable = false` | Trung bình | ✅ Đã sửa |
| U-1 | List trong `Users` bị `null` khi dùng `@Builder` | Cao | ⬜ |
| U-2 | `email` cho phép `NULL` | Trung bình | ⬜ |
| U-3 | Quan hệ ngược (`@OneToMany`/`@OneToOne`) không dùng tới trong `Users` | Thấp | ⬜ |
| U-4 | `is_active` dùng chung cho "bị khóa" và "tự xóa tài khoản" | Thấp (mở rộng) | ⬜ |
| O-1 | `Order.orderItems` dùng `CascadeType.ALL` + `orphanRemoval` | Trung bình | ⬜ |
| O-2 | `ON DELETE CASCADE` trên `order_items` (V9) và `payments` (V10) | Trung bình | ⬜ |

---

## ✅ RT-0: Migration V13 (đã sửa)

Đã xử lý:
- [x] Thiếu `;` sau `CREATE TABLE` (Flyway lỗi cú pháp)
- [x] `is_revoked DEFAULT TRUE` → `FALSE` (token mới tạo không được ở trạng thái đã thu hồi)
- [x] `jti VARCHAR(255)` → `VARCHAR(36)` (UUID dài 36 ký tự)
- [x] Bỏ index thừa trên `jti` (`UNIQUE` đã tự tạo index)
- [x] Thêm index `user_id` (PostgreSQL **không** tự index cột FK; cần cho "logout tất cả thiết bị")
- [x] FK thêm `ON DELETE CASCADE` (token không có giá trị nghiệp vụ, user mất thì token xóa theo)
- [x] Bỏ `@OneToMany refreshTokenList` khỏi `Users`
- [x] `@ManyToOne(fetch = LAZY)` trong `RefreshToken`

---

## RT-1: Đổi tên field `users` → `user` trong `RefreshToken`

- **Vấn đề:** `Address`, `Order`, `Cart` đều đặt là `user`, riêng `RefreshToken` lại là `users`.
- **Vì sao:** không thống nhất thì dễ viết sai JPQL/derived query (`findByUser_Email` với `findByUsers_Email`).
- **Hướng sửa:** dùng Rename của IntelliJ; kiểm tra lại các repository method có dùng tên field.

## RT-2: `RefreshToken` thiếu `nullable = false`

- **Vấn đề:** `@JoinColumn(name = "user_id")` và `expirationDate` chưa có `nullable = false`, trong khi DB đã là `NOT NULL`.
- **Vì sao:** entity nên phản ánh đúng ràng buộc của DB. Thiếu ràng buộc thì lỗi chỉ lộ ra lúc `INSERT` (exception từ DB, khó đọc), thay vì được bắt sớm. Một token không thuộc user nào thì vô nghĩa.
- **Hướng sửa:** thêm `nullable = false` (và cân nhắc `optional = false` cho `@ManyToOne`).

---

## U-1: List trong `Users` bị `null` khi tạo bằng `@Builder` (ưu tiên cao)

- **Vấn đề:** `private List<Address> addresses;` không có giá trị khởi tạo và không có `@Builder.Default`, nên `Users.builder()...build().getAddresses()` trả về `null`.
- **Vì sao:** đây là **gốc bệnh** của bug NPE ở cart (commit T2-004). Các đoạn `if (x.getList() == null) x.setList(new ArrayList<>())` chỉ là chữa triệu chứng.
- **Lưu ý:** chỉ viết `= new ArrayList<>()` mà **thiếu** `@Builder.Default` thì Lombok builder vẫn bỏ qua giá trị đó.
- **Hướng sửa:** rà tất cả entity có `List` + `@Builder` (Users, Cart, Product, Order…), thêm khởi tạo kèm `@Builder.Default`. Sau đó xóa các đoạn check `null` thừa trong service.

## U-2: `email` cho phép `NULL`

- **Vấn đề:** V1 khai báo `email VARCHAR(255) UNIQUE` (không có `NOT NULL`), entity cũng không có `nullable = false`.
- **Vì sao:** email là định danh để đăng nhập, user không có email thì không đăng nhập được. PostgreSQL cho phép **nhiều dòng `NULL`** trong cột `UNIQUE`, nên `UNIQUE` không chặn được.
- **Hướng sửa:**
  1. Kiểm tra dữ liệu: `SELECT count(*) FROM users WHERE email IS NULL;`
  2. Viết **migration mới** (V14+) `ALTER COLUMN email SET NOT NULL`. **Không sửa V1**, vì Flyway lưu checksum của migration đã chạy, sửa file cũ sẽ báo lỗi validate.
  3. Thêm `nullable = false` vào entity.

## U-3: Quan hệ ngược không dùng tới trong `Users`

- **Vấn đề:** `Users` giữ nhiều quan hệ ngược: `addresses`, `shops`, `cart`, `orders`, `reviewItems`. Kết quả tìm getter trong service (2026-10-02):

  | Getter | Có dùng không? |
  |---|---|
  | `getShops()` | ✅ `ShopServiceImpl` |
  | `getAddresses()` | ❌ |
  | `getOrders()` | ❌ |
  | `getReviewItems()` | ❌ |
  | `getCart()` | ❌ (chỉ có `cart.getUser()`, chiều ngược lại) |

- **Vì sao:** quan hệ ngược **không có cột nào trong DB**, chỉ là lối tắt trong Java. Rủi ro: `toString()`/`@Data`/Jackson chạm vào collection gây lazy load ngoài ý muốn, `LazyInitializationException` hoặc đệ quy vô hạn; gọi trong vòng lặp gây N+1; `Users` phình thành "god entity".
- **Hướng sửa:** với từng quan hệ, tự hỏi *"có use case nào thật sự gọi getter này không?"*. Không có thì xóa, truy vấn qua repository thay thế. **Chú ý:** `addresses` đang có `cascade = ALL, orphanRemoval = true`, cần kiểm tra luồng tạo/xóa address có dựa vào cascade này không trước khi xóa.
- **Quy tắc rút ra:** `@ManyToOne` và `@OneToOne` luôn đặt `LAZY` (mặc định là EAGER). `@OneToMany` chỉ thêm khi thật sự cần.

## U-4: `is_active` dùng chung cho 2 nghiệp vụ khác nhau (mở rộng)

- **Vấn đề:** "admin khóa" và "user tự xóa tài khoản" nếu cùng là `is_active = false` thì admin "mở khóa" có thể vô tình hồi sinh tài khoản user đã chủ động xóa.
- **Vì sao:** khác nhau về người quyết định, khả năng mở lại, xử lý dữ liệu cá nhân (ẩn danh) và việc đăng ký lại bằng email cũ.
- **Hướng sửa (gợi ý):** thay `is_active` bằng enum `status` (`ACTIVE`, `BANNED`, `DELETED`). Khi xóa tài khoản thì ẩn danh tên/SĐT, ghi đè email thành dạng `deleted_<id>@...` để giải phóng ràng buộc `UNIQUE`, và revoke toàn bộ refresh token.
- **Nguyên tắc:** e-commerce **không xóa cứng user** đã có đơn hàng; dùng soft delete.

---

## O-1: `Order.orderItems` dùng `CascadeType.ALL` + `orphanRemoval`

- **Vấn đề:** trong `OrderServiceImpl.orderFromCart`, từng `OrderItem` được lưu tay bằng `orderItemRepository.save(item)`, nên cascade PERSIST thực tế không làm gì. `REMOVE` không bao giờ dùng (đơn không bị xóa). Ngoài ra có `order.getOrderItems().clear()`: kết hợp `orphanRemoval = true`, nếu list không rỗng thì **các item sẽ bị xóa khỏi DB**.
- **Vì sao:** `CascadeType.ALL` là code smell, kiểu "cho phép mọi thứ" mà không xác định rõ mình cần gì. Commit `aa5ede3` sinh ra từ chính chỗ này.
- **Hướng sửa (chọn 1):**
  - (a) Giữ save tay, bỏ cascade và `orphanRemoval`.
  - (b) Chỉ giữ `PERSIST`, bỏ `orderItemRepository.save(item)`, add item vào `order` rồi `save(order)` một lần (phù hợp với ý "OrderItem là một phần không tách rời của Order").
  - Dù chọn hướng nào cũng nên bỏ đoạn `clear()` + `addAll()`.

## O-2: `ON DELETE CASCADE` trên dữ liệu không được phép mất

- **Vấn đề:** V9 `order_items` và V10 `payments` dùng `ON DELETE CASCADE` tới `orders`.
- **Vì sao:** lỡ chạy `DELETE FROM orders ...` thì DB **im lặng** xóa luôn item và payment. Với dữ liệu tài chính, lưới an toàn thật sự là `RESTRICT`: DB từ chối xóa và báo lỗi.
- **Hướng sửa:** viết **migration mới** để drop và tạo lại FK với `ON DELETE RESTRICT` (hoặc bỏ `ON DELETE`). **Không sửa V9/V10.**
- **Nguyên tắc:** `CASCADE` đúng hay sai phụ thuộc vào **giá trị nghiệp vụ của dữ liệu con**. Token mất không sao, nên `CASCADE`. Đơn hàng/thanh toán không được mất, nên `RESTRICT`.

---

## Bài học rút ra

1. Quan hệ JPA có 2 phía: phía có cột FK (`@ManyToOne`) là thật, phía `mappedBy` chỉ là lối tắt Java.
2. `@ManyToOne`/`@OneToOne` mặc định EAGER, nên luôn đặt LAZY.
3. `@Builder` + collection thì luôn cần `@Builder.Default`.
4. JPA cascade (Hibernate xóa từng dòng, chỉ chạy qua Java) khác DB `ON DELETE` (DB tự làm, mọi đường xóa).
5. Không bao giờ sửa migration đã chạy; luôn tạo migration mới.
6. Trước khi chọn cascade/xóa, hỏi: *"trong nghiệp vụ thật, dữ liệu cha có bao giờ bị xóa không?"*
