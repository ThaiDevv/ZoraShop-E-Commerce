# 📒 Sổ tay học tập: ZoraShop Backend

> Ghi lại **kiến thức quan trọng** và **lỗi sai của chính mình** trong quá trình làm project, để đọc lại trước khi code tiếp hoặc trước khi đi phỏng vấn.
> Cách dùng: đọc phần **Lỗi sai** trước, vì đó là những chỗ mình đã thật sự sai. Mỗi khi học thêm điều mới hoặc mắc lỗi mới thì ghi thêm vào phần tương ứng và vào **Nhật ký** cuối file.

---

## Mục lục
1. [Lỗi sai quan trọng của mình](#1-lỗi-sai-quan-trọng-của-mình)
2. [JWT, Refresh Token, Spring Security](#2-jwt-refresh-token-spring-security)
3. [JPA / Hibernate](#3-jpa--hibernate)
4. [Database, Flyway, PostgreSQL](#4-database-flyway-postgresql)
5. [Transaction và Concurrency](#5-transaction-và-concurrency)
6. [Java / Spring nói chung](#6-java--spring-nói-chung)
7. [Bảo mật cấu hình và Git](#7-bảo-mật-cấu-hình-và-git)
8. [Tư duy senior: những câu tự hỏi](#8-tư-duy-senior-những-câu-tự-hỏi)
9. [Câu hỏi phỏng vấn đã trả lời được](#9-câu-hỏi-phỏng-vấn-đã-trả-lời-được)
10. [Trạng thái hiện tại và việc tiếp theo](#10-trạng-thái-hiện-tại-và-việc-tiếp-theo)
11. [Nhật ký](#11-nhật-ký)

---

## 1. Lỗi sai quan trọng của mình

> Những lỗi này đều **đã thật sự xảy ra** trong code của mình. Đọc kỹ để không lặp lại.

### 🔴 Lỗi nghiêm trọng (gây bug thật hoặc lỗ hổng bảo mật)

| # | Lỗi | Hậu quả | Bài học |
|---|---|---|---|
| L1 | Filter gọi `filterChain.doFilter()` **2 lần** (một lần trong `try`, một lần sau `catch`) | Mỗi request có token hợp lệ chạy controller **2 lần**: tạo 2 đơn hàng, trừ kho 2 lần | Mỗi request đi qua `doFilter` **đúng 1 lần**, đặt sau `try/catch` |
| L2 | Endpoint refresh lấy user từ `Authentication` (access token) thay vì đọc refresh token trong body | Access token hết hạn (đúng lúc cần refresh) thì NPE → 500. Access token còn hạn thì cấp token mới **không cần kiểm tra refresh token**, ai trộm access token sẽ tự cấp token vô hạn | Refresh endpoint chỉ được tin **refresh token**, không dùng `Authentication` |
| L3 | Nhánh `else {}` rỗng rồi `return null` trong refresh | Token sai mà vẫn trả **200 OK**, data null: sai im lặng, còn tệ hơn lỗi compile | Không bao giờ `return null` để "cho compile qua". Trường hợp lỗi phải **ném exception** |
| L4 | Ném exception (`UnauthorizedException`) **trong filter** khi user bị khóa | Exception trong filter **không đi qua `@RestControllerAdvice`**, nên user nhận **500**, kể cả ở trang public | Filter chỉ trả lời "request này là ai". Không xác định được thì **không set authentication**, không ném lỗi |
| L5 | Refresh token và access token ký **cùng key**, không có claim `type` (giá trị mặc định của 2 secret giống nhau) | Refresh token (sống 7 ngày) dùng được như access token | Tách key **và** kiểm tra `type`: phòng thủ nhiều lớp (defense in depth) |
| L6 | File `.env` bị `git add` vào stage, secret còn có giá trị mặc định trong `application.yml` | Commit tiếp theo sẽ đẩy secret lên GitHub. Lỡ commit rồi thì secret **nằm mãi trong lịch sử git** | `.env` phải có trong `.gitignore` ngay từ đầu. Secret **không có giá trị mặc định**, thiếu thì app phải dừng |
| L7 | Migration V13: `is_revoke BOOLEAN DEFAULT TRUE` | Token vừa tạo đã ở trạng thái "bị thu hồi" | Luôn tự hỏi: *INSERT mà không truyền cột này thì giá trị là gì?* |
| L8 | Migration thiếu `;` giữa `CREATE TABLE` và `CREATE INDEX` | Flyway lỗi cú pháp, app không khởi động | Chạy thử migration trước khi coi là xong |
| L17 | Bước 6: gọi `revokeAllJti()` rồi `throw UnauthorizedException` **trong cùng method `@Transactional`** | Exception là `RuntimeException` nên transaction **rollback**, lệnh revoke toàn bộ **bị hủy**. Log vẫn ghi "bị tấn công" nhưng thực tế không token nào bị thu hồi | Ghi DB rồi ném exception trong cùng transaction thì phần ghi đó **mất**. Xem mục 5.7 |
| L18 | Kiểm tra hết hạn bị **đảo ngược**: `if (exp.isAfter(now)) throw "token is expired"` | `isAfter(now)` nghĩa là **chưa** hết hạn. Token bị dùng lại (còn hạn) rơi vào nhánh này trước, nên **nhánh phát hiện reuse không bao giờ chạy tới** | Đọc lại điều kiện bằng lời trước khi viết. Thứ tự các `if` quyết định nhánh nào chạy được |
| L19 | `@Query` dùng **tên bảng/cột SQL** (`refresh_token`, `is_revoked`, `user_id`) nhưng không có `nativeQuery = true` | `@Query` mặc định là **JPQL**, chỉ hiểu tên entity và tên field (`RefreshToken`, `isRevoked`, `user.id`). Spring kiểm tra query lúc khởi động nên **app không chạy được** | JPQL dùng tên Java; SQL thuần thì thêm `nativeQuery = true` |
| L22 | Sửa entity (`setIsActive`, `setPassword`) rồi gọi query `@Modifying(clearAutomatically = true)` **trong cùng transaction** | `clearAutomatically` xóa sạch persistence context **sau** query. Thay đổi trên entity **chưa được flush thì bị vứt đi**: admin khóa user nhưng `is_active` không đổi, đổi mật khẩu nhưng mật khẩu cũ vẫn dùng được | Dùng `flushAutomatically = true` cùng `clearAutomatically = true`, và luôn kiểm tra lại dữ liệu trong DB sau khi test |
| L23 | Gọi query `@Modifying` từ method **không có `@Transactional`** (`logout`, `logoutAll`) | Query `UPDATE`/`DELETE` tự khai báo trong repository **không tự có transaction**, nên ném `TransactionRequiredException` và trả 500 | Method service gọi query ghi dữ liệu phải có `@Transactional` |
| L24 | Dùng `@GetMapping` cho logout/logout-all (thao tác **thay đổi dữ liệu**) và gửi body trong GET | GET theo chuẩn phải **an toàn** (không đổi trạng thái): trình duyệt có thể prefetch, proxy có thể cache hoặc bỏ body | Thao tác thay đổi trạng thái dùng `POST`/`PUT`/`DELETE` |

### 🟠 Lỗi về tư duy, thiết kế

| # | Lỗi | Đúng ra là |
|---|---|---|
| L9 | Nghĩ `@OneToMany` thì "gọi user là tự load luôn token" | `@OneToMany` mặc định **LAZY**. Rủi ro thật là `toString`/Jackson vô tình chạm vào collection, N+1, và code không ai dùng |
| L10 | Nhầm `CascadeType.ALL` (JPA) với `ON DELETE CASCADE` (DB) | Hai tầng khác nhau, xem mục 3.4 |
| L11 | Nghĩ `ON DELETE CASCADE` trên `order_items`/`payments` là "lưới an toàn" | Với dữ liệu không được mất, `CASCADE` **khuếch đại sai lầm** (xóa nhầm một đơn là mất cả item và payment). Lưới an toàn thật là `RESTRICT` |
| L12 | Tạo token xong rồi **parse lại chính token đó** để lấy `jti`, `exp` | Luồng bị ngược. **Ai cần dữ liệu thì người đó tạo ra dữ liệu**: service sinh `jti` và `exp`, provider chỉ ký |
| L13 | Index thừa trên cột `UNIQUE` (`jti`), thiếu index trên FK `user_id` | `UNIQUE` đã tự tạo index. PostgreSQL **không** tự index cột FK |
| L14 | `catch (Exception e)` bọc ngoài, nuốt luôn exception do chính mình ném bên trong | Chỉ bắt đúng loại exception mình dự đoán được (`JwtException`…). Lỗi khác để `GlobalExceptionHandler` xử lý |
| L15 | Nghĩ "gộp hàm parse" là phải dùng chung secret, hoặc service phải truyền key | Hàm `private parse(token, key)` + 2 hàm public. **Key là chi tiết nội bộ**, service không được biết |
| L16 | `CascadeType.ALL` + `orphanRemoval` trên `Order.orderItems` trong khi vẫn `save()` tay từng item | Cascade gần như không làm gì, còn `clear()` có thể xóa item khỏi DB. `ALL` là code smell |
| L20 | Sau khi `revokeJti()` trả về 0, các `if` không bao phủ hết trường hợp nên code **rơi xuống dưới** và vẫn cấp token mới | Có kẽ hở cấp token mà không thu hồi token cũ (ví dụ khi đồng hồ DB và JVM lệch nhau) | Khi điều kiện "thắng" không xảy ra (0 dòng), **mọi nhánh đều phải từ chối**. Các nhánh chỉ khác nhau ở việc có revoke toàn bộ hay không |
| L21 | `catch (JwtException e) { throw new UnauthorizedException("Token is invalid"); }` **bỏ mất `e`** | Không còn cách nào biết lỗi thật (hết hạn, sai chữ ký, sai định dạng…), log chỉ ghi "Token is invalid" | Dùng **exception chaining**: `new UnauthorizedException(msg, e)` và log nguyên nhân ở một chỗ. Xem mục 6 |

### 🔵 Lỗi nhỏ nhưng hay lặp lại

- `assert x != null;` để kiểm tra null: **Java tắt `assert` mặc định** (cần cờ `-ea`), nên trên môi trường thật nó không làm gì.
- `log.warn("message: ", e.getMessage())` thiếu `{}`, nên tham số **không được in ra**.
- Khai báo hằng số nhưng vẫn gõ tay chuỗi ở chỗ khác: đổi hằng số là chỗ so sánh bị lệch mà compiler không báo.
- `claims.get("type").equals("X")` có thể ném NPE. Viết ngược lại thành `"X".equals(claims.get("type"))`.
- Đặt tên sai chính tả: `issueAccressToken`, package `oder`, `catagory`, `UpdateShopRequire`. Dùng `Shift+F6` để rename.
- Ép kiểu cùng một object 2 lần thành 2 biến; viết `if (x == null)` **sau** dòng đã dùng `x`.
- Tham số tên `refreshToken` trong hàm dùng chung cho cả 2 loại token.
- `@Value` cùng một cấu hình khai báo ở 2 class. Chỉ để ở nơi thật sự dùng.
- Mô tả trong `SwaggerConfig` ghi "nhập dạng `Bearer <token>`", nhưng scheme `bearer` đã **tự thêm tiền tố**. Làm theo mô tả thì header thành `Bearer Bearer xxx` và filter trả 401. Ô Authorize chỉ dán **token trần**.

---

## 2. JWT, Refresh Token, Spring Security

### 2.1. Whitelist hay blacklist cho refresh token
- **Whitelist** (lưu token còn hợp lệ) **tốt hơn** cho refresh token: làm được logout tất cả thiết bị, rotation, reuse detection.
- **Blacklist** hợp với **access token** (Redis + TTL), làm sau.
- Refresh token vốn đã phải tra DB mỗi lần dùng, nên thêm bảng không tốn thêm gì.

### 2.2. Lưu `jti`, không lưu chuỗi token
- `jti` (JWT ID) là một UUID. Cột ngắn, index nhẹ.
- DB bị lộ thì hacker cũng không dùng được, vì muốn có token hợp lệ chứa `jti` đó thì phải có **secret key** để ký.
- Đặt bằng `.id(jti)` của builder, đọc bằng `claims.getId()`. Các claim chuẩn: `sub`, `exp`, `iat`, `jti`. Đừng tự thêm `email`, `expiration` trùng nghĩa.

### 2.3. Hai tầng "hợp lệ"
| Tầng | Ai kiểm tra | Nội dung |
|---|---|---|
| **Mật mã** | `parseSignedClaims()` tự làm | Đúng chữ ký, chưa hết hạn, đúng định dạng. Sai thì ném `JwtException` |
| **Nghiệp vụ** | **Mình tự làm** | Đúng `type`; `jti` có trong DB và chưa revoke; user còn active |

→ Không cần hàm `validate()` trả `boolean` riêng: **parse một lần**, sai thì exception.
→ Ví dụ: user logout lúc 9h, kẻ trộm dùng lại token lúc 10h. Chữ ký vẫn đúng, nên **chỉ có bước tra DB mới chặn được**.

### 2.4. Nguyên tắc của filter (`OncePerRequestFilter`)
- Filter chạy cho **mọi request**, kể cả trang public.
- Nhiệm vụ duy nhất: xác định "request này là ai". Không xác định được thì coi là khách, **Spring Security tự quyết** endpoint có cho khách vào hay không.
- **Không ném exception** ra khỏi filter (exception ở đây không tới được `@RestControllerAdvice`).
- `doFilter` đúng **1 lần**.
- Kiểm tra `isEnabled()`: user bị khóa thì access token cũ cũng phải mất tác dụng.

### 2.5. Endpoint refresh
- Phải nằm trong `permitAll`, vì lúc cần refresh thì access token đã chết.
- Chỉ tin **refresh token trong body**. Không dùng `Authentication`.
- Rotation: mỗi refresh token dùng **1 lần**; client phải lưu token mới.
- Reuse detection: token đã revoke mà bị dùng lại thì **thu hồi toàn bộ token của user**.
- Logout nên **idempotent**: token sai hoặc đã revoke vẫn trả 204.

### 2.6. Thời hạn access token
- 24h là quá dài. Nên để 15–30 phút.
- Logout xong, access token **vẫn dùng được đến khi hết hạn**. Thời hạn ngắn giúp giảm rủi ro này. Muốn chặn ngay thì cần blacklist (Redis).

### 2.7. Encapsulation trong `JwtTokenProvider`
- `private Claims parse(String token, SecretKey key)` + `public parseAccessToken()` / `parseRefreshToken()`.
- Kiểm tra `type` đặt **bên trong** hàm public để không ai quên.
- Provider ném `JwtException`; **bên gọi tự quyết** ý nghĩa: filter thì bỏ qua, service refresh thì trả 401.

### 2.8. Logout phải idempotent
- **Idempotent** = gọi 1 lần hay N lần thì **kết quả cuối cùng như nhau**.
- Mục tiêu của logout là "token này không dùng được nữa". Nếu token đã hết hạn hoặc đã bị revoke thì mục tiêu **đã đạt rồi**, nên trả **204** thay vì báo lỗi.
- Lợi ích:
  1. **Client retry an toàn.** Mạng chập chờn, client gửi logout 2 lần; lần 2 không được làm frontend báo lỗi.
  2. **Frontend không bị kẹt.** Nếu logout trả 401 vì token đã hết hạn thì user bấm "Đăng xuất" lại thấy lỗi, rất vô lý.
  3. **Không lộ thông tin.** Trả lỗi khác nhau cho "token sai" và "token đúng" giúp kẻ tấn công dò xem token nào còn sống.
- Liên hệ HTTP: `GET`, `PUT`, `DELETE` theo chuẩn là idempotent; `POST` thì không, nhưng có thể **thiết kế** để idempotent như logout.

### 2.9. Logout: backend làm gì, frontend làm gì
- **Backend:** thu hồi refresh token trong DB (logout = 1 token, logout-all = mọi token của user), trả 204.
- **Frontend:** gọi API logout, rồi **luôn** xóa token khỏi nơi lưu (kể cả khi API lỗi hoặc mất mạng), rồi chuyển về trang login.
- Access token **không thu hồi được** từ backend (không có blacklist), nó vẫn sống tối đa 15 phút. Vì vậy FE phải tự xóa nó, và thời hạn ngắn là lớp bảo vệ.

### 2.10. Frontend lưu token ở đâu
Hiện tại backend trả **cả 2 token trong JSON body**, nên FE phải tự lưu bằng JavaScript.

| Nơi lưu | XSS đọc được không? | Reload trang | Ghi chú |
|---|---|---|---|
| `localStorage` | ❌ Đọc được | Còn | Đơn giản nhất, cũng rủi ro nhất cho refresh token (sống 7 ngày) |
| `sessionStorage` | ❌ Đọc được | Còn (mất khi đóng tab) | Giống `localStorage` về bảo mật |
| Biến trong bộ nhớ JS | Khó hơn | **Mất** | Hợp cho **access token** (sống ngắn) |
| **Cookie `HttpOnly`** | ✅ JS không đọc được | Còn | Hợp cho **refresh token**. Cần backend `Set-Cookie`, CORS `allowCredentials`, và cân nhắc CSRF (`SameSite`) |

- Mô hình phổ biến: **access token trong bộ nhớ, refresh token trong cookie `HttpOnly; Secure; SameSite=Strict; Path=/api/v1/auth`**. Reload trang thì gọi `/refresh` (cookie tự gửi) để lấy access token mới.
- Đổi sang cookie là thay đổi ở **backend** (login/refresh trả `Set-Cookie`, refresh/logout đọc cookie thay vì body), không chỉ ở FE.

### 2.11. HTTP method cho thao tác thay đổi dữ liệu
- `GET` phải **an toàn** (chỉ đọc) và không nên có body. Trình duyệt, proxy, cache đều được phép giả định điều đó.
- Logout, logout-all, đổi mật khẩu… dùng `POST`.
- Đặt endpoint cần đăng nhập (logout-all) **ngoài** nhóm `/auth/**` public, ví dụ `/api/v1/users/me/logout-all`, để sau này mở `permitAll("/api/v1/auth/**")` không vô tình public nó.
- Service không nên nhận `Authentication` (đối tượng của tầng web/security). Controller lấy `userId`/email rồi truyền xuống; service dễ test và dùng lại hơn.

### 2.12. Kiểm tra token rỗng: `isEmpty()`, `isBlank()` và `StringUtils.hasText()`

| Giá trị token sau `Bearer ` | `String.isEmpty()` | `String.isBlank()` | `StringUtils.hasText()` |
|---|---:|---:|---:|
| `""` | `true` | `true` | `false` |
| `"   "` | `false` | `true` | `false` |
| `"jwt-token"` | `false` | `false` | `true` |
| `null` | Ném `NullPointerException` | Ném `NullPointerException` | `false` |

- `isEmpty()` chỉ kiểm tra độ dài bằng `0`, nên **không bắt được chuỗi toàn dấu cách**.
- `isBlank()` bắt được chuỗi rỗng hoặc chỉ có whitespace, nhưng object phải khác `null`.
- `StringUtils.hasText()` của Spring chỉ trả `true` khi chuỗi khác `null`, không rỗng và có ít nhất một ký tự không phải whitespace. Nó phù hợp với dữ liệu request có thể `null`.
- Với nguyên header `"Bearer    "`, cả `isBlank()` và `hasText()` vẫn coi là có nội dung vì có chữ `Bearer`. Phải tách phần token trước rồi mới kiểm tra:

```java
String token = authHeader.substring(7);
if (!StringUtils.hasText(token)) {
    filterChain.doFilter(request, response);
    return;
}
```

### 2.13. `CustomUserDetails`: `Users` được truyền vào ở đâu?

```java
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
    private final Users user;
}
```

`@RequiredArgsConstructor` của Lombok sinh constructor tương đương:

```java
public CustomUserDetails(Users user) {
    this.user = user;
}
```

Trong `CustomUserDetailsService`, entity được lấy từ repository rồi truyền vào constructor:

```java
Users user = userRepository.findByEmail(email);
return new CustomUserDetails(user);
```

- Đây **không phải constructor DI của Spring** vì `CustomUserDetails` không phải Spring bean; nó được tạo thủ công bằng `new` cho từng user.
- `CustomUserDetailsService` mới dùng constructor DI: class có `@Service`, còn `@RequiredArgsConstructor` sinh constructor nhận `UserRepository`; Spring tạo service và inject repository vào đó.

### 2.14. `Authentication`, `getPrincipal()` và `getName()`

Luồng đăng nhập bằng email/password:

```text
email + password
    ↓
AuthenticationManager.authenticate(...)
    ↓
CustomUserDetailsService.loadUserByUsername(email)
    ↓
UserRepository tìm Users
    ↓
new CustomUserDetails(user)
    ↓
Spring Security kiểm tra password
    ↓
Authentication đã xác thực, principal = CustomUserDetails
```

`CustomUserDetailsService` chỉ tải user và trả về `CustomUserDetails`; nó **không tự gắn** object này vào `Authentication`. Khi login, `AuthenticationProvider` của Spring Security làm việc đó sau khi kiểm tra password. Với request JWT tiếp theo, `JwtAuthenticationFilter` tự tạo `UsernamePasswordAuthenticationToken` và đặt nó vào `SecurityContextHolder`:

```java
UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

SecurityContextHolder.getContext().setAuthentication(authentication);
```

Lấy toàn bộ thông tin user:

```java
CustomUserDetails details =
        (CustomUserDetails) authentication.getPrincipal();

Users user = details.getUser();
```

`getPrincipal()` trả về `Object`, nên cần ép kiểu. Ép trực tiếp có thể gây `ClassCastException` nếu principal là anonymous hoặc được tạo theo kiểu khác. Có thể dùng pattern matching để kiểm tra an toàn:

```java
if (!(authentication.getPrincipal() instanceof CustomUserDetails details)) {
    throw new UnauthorizedException("User is not authenticated");
}
```

Nếu chỉ cần email/username:

```java
String email = authentication.getName();
```

Với principal là `UserDetails`, `getName()` lấy giá trị từ `UserDetails.getUsername()`. Trong project, `CustomUserDetails.getUsername()` trả về `user.getEmail()`, nên `authentication.getName()` trả về email. Với request anonymous, giá trị có thể là `"anonymousUser"`.

### 2.15. Vì sao `/logout` có thể public nhưng `/logout-all` phải protected?

Hai endpoint dùng hai nguồn xác thực khác nhau:

| Endpoint | Dựa vào dữ liệu nào? | Có cần access token còn hạn? |
|---|---|---:|
| `/api/v1/auth/logout` | Refresh token trong request body | Không |
| `/api/v1/auth/logout-all` | `Authentication` của request hiện tại | Có |

`/logout` chỉ thu hồi đúng refresh token được gửi trong body:

```java
public void logout(RefreshTokenRequest request) {
    Claims claims = jwtTokenProvider.parseRefreshToken(request.getRefreshToken());
    refreshTokenRepository.revokeJti(claims.getId(), now);
}
```

Tình huống thực tế:

```text
Access token:  đã hết hạn
Refresh token: vẫn còn hạn
User bấm Logout
```

User vẫn có thể đang ở trong app khi access token hết hạn vì giao diện frontend và trạng thái xác thực trên backend là hai thứ khác nhau. Ví dụ:

- User mở trang lúc token còn hạn, để tab đó 20 phút; giao diện vẫn còn nhưng access token 15 phút đã hết hạn.
- Laptop sleep rồi mở lại tab cũ.
- Frontend lưu token trong `localStorage`; reload trang vẫn thấy token tồn tại nhưng chưa kiểm tra `exp`.
- Token còn hạn lúc nút Logout được hiển thị nhưng hết hạn ngay trước khi request tới server.
- Frontend chỉ biết token hết hạn sau khi một API trả `401`, rồi mới chạy luồng dọn phiên/logout.

JWT hết hạn không tự đóng trang, tự xóa token hay tự chuyển user về màn hình login. Frontend phải chủ động xử lý những việc đó.

Nếu `/logout` yêu cầu `.authenticated()`, JWT filter không xác thực được access token đã hết hạn. Spring Security trả `401` **trước khi controller chạy**, nên refresh token trong body chưa được thu hồi. Vì mục tiêu của logout là làm refresh token mất hiệu lực, endpoint này nên `permitAll` và tự kiểm tra refresh token trong body. Token sai, hết hạn hoặc đã revoke vẫn có thể trả `204` để giữ tính idempotent.

Luồng logout phía frontend nên là:

```text
Gọi /logout bằng refresh token
    ↓
Dù API thành công, 401, 500 hay mất mạng
    ↓
Frontend vẫn xóa access token + refresh token cục bộ
    ↓
Chuyển về màn hình login
```

Backend revoke refresh token để token bị đánh cắp không dùng lại được; frontend xóa token để kết thúc phiên trên thiết bị hiện tại. Hai việc này bổ sung cho nhau.

Cho `/logout` là `permitAll` không có nghĩa client có thể tùy ý logout người khác: muốn thu hồi một token thì client phải có chính chuỗi refresh token đó. Không nhận `userId` hoặc email từ request để quyết định token cần thu hồi.

`/logout-all` khác ở chỗ nó lấy user từ security context:

```java
public void logoutAll(Authentication authentication) {
    CustomUserDetails details =
            (CustomUserDetails) authentication.getPrincipal();
    refreshTokenRepository.revokeAllJti(details.getUser());
}
```

Endpoint này cần access token hợp lệ để biết **ai** đang yêu cầu thu hồi toàn bộ phiên. Nếu vô tình `permitAll`, request anonymous có thể làm `authentication` là `null`, hoặc principal là chuỗi `"anonymousUser"`; code hiện tại có thể ném `NullPointerException` hoặc `ClassCastException` thay vì trả `401`.

Cấu hình rõ ràng:

```java
.requestMatchers(
        "/api/v1/auth/register",
        "/api/v1/auth/login",
        "/api/v1/auth/refresh-token",
        "/api/v1/auth/logout"
).permitAll()
.requestMatchers("/api/v1/auth/logout-all").authenticated()
.anyRequest().authenticated()
```

An toàn hơn về thiết kế URL là chuyển logout-all ra khỏi nhóm auth public:

```text
POST /api/v1/users/me/logout-all   → authenticated
```

Khi đó nếu sau này cấu hình `permitAll("/api/v1/auth/**")`, logout-all cũng không bị mở public ngoài ý muốn.

### 2.16. Không truyền `Authentication` xuống service và không sửa entity lấy từ principal

`Authentication` là đối tượng thuộc Spring Security. Controller là tầng tiếp xúc với HTTP/Security nên có thể đọc nó, nhưng service nên nhận dữ liệu nghiệp vụ đơn giản như `userId` hoặc email:

```text
Controller: Authentication → lấy email/userId
Service:    nhận email/userId → xử lý nghiệp vụ
```

Code đang có:

```java
public Boolean changePassword(
        Authentication authentication,
        ChangePasswordRequest request
) {
    CustomUserDetails details =
            (CustomUserDetails) authentication.getPrincipal();
    Users user = details.getUser();
    // ...
}
```

Nhược điểm:

- Service phụ thuộc trực tiếp Spring Security, khó dùng lại từ job, event listener hoặc một luồng không có HTTP request.
- Unit test phải dựng/mock `Authentication`, principal và `CustomUserDetails` dù test chỉ quan tâm nghiệp vụ đổi mật khẩu.
- Service tự ép kiểu principal nên có nguy cơ `ClassCastException` nếu loại authentication thay đổi.

Cách tách tầng:

```java
@PutMapping("/me/password")
public ApiResponse<Boolean> changePassword(
        Authentication authentication,
        @Valid @RequestBody ChangePasswordRequest request
) {
    return ApiResponse.success(
            userService.changePassword(authentication.getName(), request)
    );
}
```

```java
@Transactional
public Boolean changePassword(
        String email,
        ChangePasswordRequest request
) {
    Users user = userRepository.findByEmail(email);
    if (user == null) {
        throw new ResourceNotFoundException("User not found");
    }

    if (!passwordEncoder.matches(
            request.getOldPassword(),
            user.getPassword()
    )) {
        throw new UnauthorizedException("Old password doesn't match");
    }

    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    refreshTokenRepository.revokeAllJti(user);
    return true;
}
```

Entity `Users` nằm trong `CustomUserDetails` được load lúc `JwtAuthenticationFilter` chạy, tức là **trước khi** transaction của method service bắt đầu. Nó không nên được coi là entity managed của persistence context trong service; thường nó đã detached hoặc ít nhất thuộc một persistence context khác.

Nếu lấy object đó rồi sửa:

```java
Users user = details.getUser();
user.setPassword(newPassword);
```

dirty checking của transaction service không được đảm bảo trên object này. Gọi `save(user)` thường sẽ `merge`, nhưng có thể ghi một snapshot cũ và che mất thay đổi vừa được transaction khác commit.

Load lại user trong method `@Transactional` giúp:

- Lấy trạng thái mới nhất từ database.
- Entity thuộc persistence context hiện tại và được dirty checking theo dõi.
- Kiểm tra password/trạng thái account trên dữ liệu hiện tại.
- Dễ kết hợp `@Version` hoặc locking nếu cần xử lý concurrent update.

Với entity đã được load và đang managed trong transaction, chỉ cần `user.setPassword(...)`; không bắt buộc gọi lại `userRepository.save(user)` vì Hibernate sẽ flush thay đổi khi commit.

`updateProfile` trong project đã dùng đúng hướng này: controller lấy `authentication.getName()`, service nhận email rồi gọi `userRepository.findByEmail(email)`. `changePassword` và `logoutAll` nên nhất quán theo cùng cách.

---

## 3. JPA / Hibernate

### 3.1. Hai phía của quan hệ
- Phía có cột FK (`@ManyToOne`) là phía **thật**, ánh xạ đúng cột trong DB.
- Phía `mappedBy` (`@OneToMany`) **không có cột nào**, chỉ là lối tắt trong Java (Hibernate tự chạy `SELECT ... WHERE user_id = ?`).
- Xóa `@OneToMany` đi thì **DB không thay đổi gì**.

### 3.2. Fetch mặc định
| Annotation | Mặc định | Nên |
|---|---|---|
| `@ManyToOne`, `@OneToOne` | **EAGER** | Luôn đặt `LAZY` |
| `@OneToMany`, `@ManyToMany` | LAZY | Chỉ thêm khi thật sự dùng tới |

### 3.3. Trước khi thêm `@OneToMany`, hỏi: "có ai gọi getter này không?"
Rủi ro: `toString()`/`@Data`/Jackson chạm vào collection gây lazy load, `LazyInitializationException`, đệ quy vô hạn; gọi trong vòng lặp gây N+1; entity phình thành "god entity".

### 3.4. JPA cascade và DB `ON DELETE`
| | JPA `CascadeType.REMOVE` | DB `ON DELETE CASCADE` |
|---|---|---|
| Ai làm | Hibernate | PostgreSQL |
| Cách làm | Load hết con vào RAM, xóa từng dòng | Một lệnh |
| Khi nào chạy | Chỉ khi xóa qua `EntityManager` | Mọi cách xóa |
| Cần `@OneToMany` không | **Cần** | Không |

- JPA cascade (`PERSIST`) có ích khi **thao tác con qua cha**: `order.getItems().add(...)` rồi `save(order)`.
- Không viết `CascadeType.ALL` theo thói quen. Chọn đúng loại cần dùng.

### 3.5. `@Builder` + collection
- `List<X> list;` không khởi tạo thì `builder().build()` cho ra `list = null`, dẫn tới NPE. Đây là gốc của bug NPE ở cart (T2-004).
- Phải viết `@Builder.Default private List<X> list = new ArrayList<>();`. Thiếu `@Builder.Default` thì Lombok **bỏ qua** giá trị khởi tạo.

### 3.6. Hard delete và soft delete
- E-commerce **không xóa cứng user** đã có đơn hàng: mất doanh thu, kế toán sai, mất review, mất bằng chứng khi tranh chấp.
- Dùng soft delete. "Bị admin khóa" khác "tự xóa tài khoản", nên dùng enum `status` (`ACTIVE`/`BANNED`/`DELETED`) thay vì chung một `is_active`.
- Xóa tài khoản: ẩn danh thông tin cá nhân, ghi đè email thành `deleted_<id>@...` để giải phóng `UNIQUE`.

### 3.7. Snapshot trong `order_items`
- Lưu tên, giá, ảnh **tại thời điểm mua**, nên seller đổi giá sau đó cũng không làm sai lịch sử đơn. (Phần này mình đã làm đúng.)

---

## 4. Database, Flyway, PostgreSQL

- Cột `UNIQUE` **đã tự có index**, đừng tạo thêm.
- PostgreSQL **không tự index cột FK** (MySQL InnoDB thì có). Phải tự tạo index cho các cột hay dùng để lọc.
- `UNIQUE` cho phép **nhiều dòng `NULL`**. Muốn bắt buộc có giá trị thì thêm `NOT NULL`.
- **Không bao giờ sửa migration đã chạy.** Flyway lưu checksum, sửa file cũ sẽ báo lỗi validate. Luôn tạo migration mới (V14, V15…).
- Chọn `ON DELETE` theo **giá trị nghiệp vụ của dữ liệu con**: token mất không sao thì `CASCADE`; đơn hàng, thanh toán thì `RESTRICT`.
- Entity nên phản ánh đúng ràng buộc của DB (`nullable = false` khớp với `NOT NULL`).
- Thời gian: `Instant` là thời điểm tuyệt đối (UTC), khớp với JWT `exp`. `LocalDateTime` không có múi giờ. `java.util.Date` là API cũ. Không dùng lẫn lộn trong cùng một entity.
- Cẩn thận với từ khóa JPQL khi đặt alias (ví dụ `or`, `order`, `group`).
- JPQL dùng **tên class entity** (`RefreshToken`), không dùng tên trong `@Table(name = "refresh_token")`. Field cũng dùng tên Java (`isRevoked`, `expirationDate`). Quan hệ thì so sánh cả entity (`rf.user = :user`) hoặc đi qua field (`rf.user.id = :userId`).
- Trong query có điều kiện thời gian, nên truyền `:now` từ Java thay vì `CURRENT_TIMESTAMP`, để **chỉ có một đồng hồ** (JVM) quyết định hết hạn, khớp với cách JJWT kiểm tra `exp`.

---

## 5. Transaction và Concurrency

### 5.1. Race condition kiểu find → check → save
```
Request A: find → revoked = false → OK
Request B: find → revoked = false → OK   ← cả hai cùng qua
```
**Cách đúng:** dùng câu `UPDATE` có điều kiện, trả về số dòng bị ảnh hưởng:
`UPDATE ... SET revoked = true WHERE jti = ? AND revoked = false` → trả về 0 nghĩa là request khác đã thắng.
Áp dụng cho cả tồn kho: `UPDATE inventory SET reserved = reserved + :qty WHERE ... AND quantity - reserved >= :qty`.

### 5.2. `@Transactional`
- Mọi thao tác **thành công cùng nhau hoặc không có gì xảy ra**.
- Không có `@Transactional` ở method thì `repository.save()` tự commit ngay.
- Mặc định **chỉ rollback với `RuntimeException`**, checked exception thì không.
- **Bẫy (sẽ gặp ở Bước 6):** đã ghi "revoke tất cả" rồi ném exception thì rollback **xóa luôn** lệnh revoke đó. Giải pháp: `noRollbackFor`, hoặc `REQUIRES_NEW` ở một bean khác.
- **Self-invocation:** gọi method `@Transactional` trong **cùng class** thì annotation không có tác dụng (vì không đi qua proxy).

### 5.3. Lưu DB trước hay ký token trước
- Ký trước, lưu lỗi: client không nhận được token; nếu token có lọt ra thì `jti` cũng không có trong DB, nên vô dụng. **An toàn nhờ whitelist.**
- Lưu trước, ký lỗi: có dòng rác trong DB. `@Transactional` sẽ rollback, nên DB sạch.
- Nguyên tắc: **token chỉ có giá trị khi DB xác nhận.**

### 5.4. Khác
- `@Version` (optimistic lock) ngăn được bán vượt tồn kho, nhưng nếu không bắt `ObjectOptimisticLockingFailureException` thì user nhận **500**. Nên trả 409 hoặc retry.
- Lock nhiều dòng thì **sắp xếp theo id** trước, để tránh deadlock.

### 5.5. Rotation: bước revoke thành công nhưng bước cấp token mới lỗi
Luồng refresh: (1) parse → (2) lấy `jti` → (3) **revoke token cũ** → (4) kiểm tra user → (5) **cấp token mới**.
- Nếu (3) thành công mà (5) lỗi (ví dụ DB lỗi khi lưu token mới), exception bay ra, `@Transactional` **rollback cả (3)**. Token cũ **trở lại trạng thái chưa revoke**.
- Điều này **tốt cho user**: user không bị đăng xuất oan vì lỗi của server, chỉ cần thử refresh lại bằng token cũ.
- Không có lỗ hổng: token mới chưa từng được gửi cho client (exception bay ra trước `return`).
- Nếu **không có** transaction: token cũ đã bị revoke, token mới không tồn tại, nên user **mất phiên đăng nhập** và phải login lại.
- → Đây chính là ý nghĩa của **atomicity**: "revoke cũ + cấp mới" là **một** thao tác, hoặc cả hai, hoặc không gì cả.

### 5.6. Vì sao dùng `UPDATE` có điều kiện thay vì find → if → save
**Cách sai:**
```
Thread A: findByJti → revoked = false ┐
Thread B: findByJti → revoked = false ┘ cả hai đọc trước khi ai kịp ghi
Thread A: setRevoked(true), save, cấp token mới
Thread B: setRevoked(true), save, cấp token mới   ← 1 token cũ đổi được 2 cặp token mới
```
Hậu quả: kẻ trộm và user thật cùng refresh một lúc thì **cả hai đều có phiên mới**, reuse detection không phát hiện được gì.

**Cách đúng:** `UPDATE refresh_token SET is_revoked = true WHERE jti = ? AND is_revoked = false` rồi xem **số dòng bị ảnh hưởng**.
- PostgreSQL **khóa dòng** khi `UPDATE`. Request B phải **chờ** A commit xong.
- Sau khi A commit, PostgreSQL **kiểm tra lại điều kiện `WHERE`** trên phiên bản mới của dòng. Lúc này `is_revoked = true` nên không khớp, B nhận **0 dòng**.
- → "Kiểm tra" và "ghi" xảy ra trong **một câu lệnh nguyên tử**, không có khoảng hở nào giữa chúng.
- Đây gọi là lỗi **check-then-act** (hay TOCTOU: time-of-check to time-of-use). Gặp ở mọi nơi: trừ tồn kho, dùng voucher, rút tiền.

### 5.7. Bẫy rollback: ghi DB rồi ném exception trong cùng transaction
```
@Transactional
refreshToken() {
    revokeAllByUserId(userId);              ← ghi vào DB (chưa commit)
    throw new UnauthorizedException(...);   ← RuntimeException
}
```
**Cơ chế:** Spring bọc method bằng một proxy. Proxy mở transaction, gọi method thật, **bắt exception**. Thấy `RuntimeException` thì **rollback**, rồi ném exception tiếp ra ngoài.
→ Lệnh `UPDATE` revoke toàn bộ **chưa bao giờ được commit**. Kết quả: log ghi "phát hiện tấn công" nhưng DB không đổi gì. (Lỗi L17 của mình.)

**Cách tự kiểm chứng:** login (A) → refresh bằng A (nhận B) → refresh lại bằng A (401) → refresh bằng **B**. Nếu B **vẫn dùng được** thì lệnh revoke toàn bộ đã bị rollback.

**3 cách giải quyết:**

| Cách | Làm thế nào | Ưu | Nhược |
|---|---|---|---|
| `@Transactional(noRollbackFor = UnauthorizedException.class)` | Báo Spring: gặp exception này thì **vẫn commit** | Sửa 1 dòng | Áp dụng cho **mọi** chỗ ném `UnauthorizedException` trong method. Phải kiểm tra từng nhánh xem commit "nửa chừng" có an toàn không |
| `Propagation.REQUIRES_NEW` ở **một bean khác** | Revoke toàn bộ chạy trong **transaction riêng**, commit ngay, không bị transaction ngoài kéo theo khi rollback | Ý nghĩa rõ ràng: "sự kiện bảo mật phải được ghi dù có chuyện gì" | Cần tách class (vì self-invocation, xem 5.8). Dùng thêm 1 connection DB. Nếu transaction ngoài đã khóa đúng các dòng đó thì transaction trong sẽ chờ mãi (**tự deadlock**) |
| **Không ném exception trong transaction** | Method `@Transactional` trả về kết quả (ví dụ enum `REUSED`, `OK`…), lớp bên ngoài (không có transaction) mới ném exception | Không phụ thuộc cấu hình rollback, dễ test | Phải chia lại cấu trúc code |

- Với project này, `REQUIRES_NEW` (tách một bean như `RefreshTokenRevoker`) hoặc `noRollbackFor` đều chấp nhận được. Quan trọng là **giải thích được vì sao chọn**.
- **Mình đã chọn `noRollbackFor = UnauthorizedException.class`** (2026-10-11). Khi dùng cách này phải rà **từng chỗ** có thể ném `UnauthorizedException`, và hỏi: *tại thời điểm đó DB đã bị ghi gì chưa? commit phần đó có an toàn không?*
- Cẩn thận với `catch` **đổi loại exception**: nếu `catch (JwtException) → throw UnauthorizedException` bọc cả đoạn ghi DB, thì một lỗi JWT xảy ra **sau** khi đã ghi (ví dụ lỗi ký token) cũng bị đổi thành loại "không rollback", dẫn tới commit nửa chừng. → Thu hẹp `try` chỉ quanh bước parse.
- Nhớ: `@Transactional` mặc định rollback với `RuntimeException` và `Error`, **không** rollback với checked exception (có thể đổi bằng `rollbackFor`).

### 5.9. `@Modifying`: transaction, flush và clear
- Query `@Modifying` tự khai báo **không tự có transaction** (khác với `save()`, `delete()` có sẵn của `JpaRepository`). Method gọi nó phải có `@Transactional`.
- Bulk `UPDATE`/`DELETE` chạy **thẳng xuống DB**, bỏ qua persistence context. Entity đang nằm trong bộ nhớ không biết DB đã đổi.
- `clearAutomatically = true`: **sau** query thì xóa persistence context, để lần đọc sau lấy dữ liệu mới từ DB. Nhưng **mọi thay đổi chưa flush cũng bị xóa theo**.
- `flushAutomatically = true`: **trước** query thì đẩy thay đổi đang chờ xuống DB.
- → Khi trong cùng transaction vừa sửa entity vừa chạy bulk query: bật **cả hai**.
- `repository.save(entity)` với entity đã tồn tại **không có nghĩa là đã ghi xuống DB ngay**. Nó chỉ đưa vào persistence context; DB được ghi khi flush hoặc commit.

Thứ tự thực thi trong Spring Data JPA:

```java
if (flushAutomatically) {
    entityManager.flush();             // 1. Trước modifying query
}

int result = query.executeUpdate();     // 2. Chạy UPDATE/DELETE

if (clearAutomatically) {
    entityManager.clear();             // 3. Sau modifying query
}
```

`flush()` chỉ đồng bộ các thay đổi đang chờ trong persistence context xuống database; nó **không phải commit**. Transaction vẫn có thể rollback toàn bộ.

```java
@Modifying(
        flushAutomatically = true,
        clearAutomatically = true
)
@Query("update Users u set u.isActive = false where u.id = :id")
int deactivateById(@Param("id") Long id);
```

Giá trị mặc định của cả `flushAutomatically` và `clearAutomatically` đều là `false`. Tuy nhiên, `flushAutomatically = false` chỉ có nghĩa Spring Data không chủ động gọi `EntityManager.flush()` tại đây; Hibernate vẫn có thể tự flush tùy `FlushModeType` (thường là `AUTO`).

### 5.8. Self-invocation: vì sao gọi `@Transactional` trong cùng class không có tác dụng
```
Controller ──► [Proxy của UserServiceImpl] ──► UserServiceImpl thật
                 ↑ mở/commit/rollback transaction ở đây
```
- Spring không sửa code của mình. Nó tạo một **proxy** bọc ngoài bean, và mọi xử lý `@Transactional` nằm trong proxy.
- Bên ngoài gọi vào thì đi **qua proxy**, annotation có tác dụng.
- Bên trong class gọi `this.revokeAll()` thì gọi **thẳng object thật**, **không qua proxy**, nên `@Transactional(propagation = REQUIRES_NEW)` trên `revokeAll()` **bị bỏ qua hoàn toàn**, không báo lỗi gì.
- Cách xử lý: đặt method đó ở **một bean khác** rồi inject vào. (Cách khác như tự inject chính mình hay dùng `TransactionTemplate` cũng được, nhưng tách bean là rõ ràng nhất.)
- Áp dụng cho mọi annotation dựa trên proxy: `@Transactional`, `@Async`, `@Cacheable`, `@Retryable`.
- **Hiểu đúng:** self-invocation **không** có nghĩa là method con "không có transaction". Code của method con vẫn chạy **bên trong transaction của method cha** (cùng thread, cùng connection). Thứ bị bỏ qua là **cấu hình riêng** của method con (`REQUIRES_NEW`, `noRollbackFor`, `readOnly`, `timeout`…).
  - Ví dụ `changePassword()` gọi `this.logoutAll()`: chỉ có **một** transaction. Lỗi xảy ra ở bất kỳ đâu thì **cả hai** đều rollback (mật khẩu mới và việc revoke token).
  - Gọi `logoutAll()` thẳng từ controller (đi qua proxy) thì `@Transactional` của nó mới mở transaction riêng.

Ví dụ hai method trong cùng một class:

```java
@Transactional
public void methodA() {
    methodB();
}

@Transactional(propagation = Propagation.REQUIRES_NEW)
public void methodB() {
    // cập nhật DB
}
```

Khi gọi `methodA()` từ bean bên ngoài:

1. Lời gọi đi qua proxy nên transaction của A được mở.
2. A gọi B trực tiếp trên cùng object, không đi qua proxy.
3. Code trong B **vẫn chạy**, và thao tác DB của B vẫn nằm trong transaction đang có của A.
4. Riêng cấu hình `@Transactional` trên B **không được áp dụng**. Trong ví dụ trên, `REQUIRES_NEW` không tạo transaction mới.

Nếu A không có transaction thì B cũng chạy nhưng không được annotation của B mở transaction. Query ghi dữ liệu cần transaction có thể ném `TransactionRequiredException`.

Nếu A và B là **hai Spring bean khác nhau**, A inject B rồi gọi `b.methodB()`, lời gọi đi qua proxy của B nên annotation của B có hiệu lực:

| Cấu hình của B | Kết quả khi A đang có transaction |
|---|---|
| `REQUIRED` (mặc định) | B tham gia transaction của A |
| `REQUIRES_NEW` | Tạm dừng transaction A, mở transaction mới cho B |
| `MANDATORY` | Dùng transaction A; nếu không có thì ném lỗi |
| `NOT_SUPPORTED` | Tạm dừng transaction A và chạy B không có transaction |

Điểm quyết định không phải là "cùng file" mà là lời gọi có **đi qua Spring proxy** hay không. Hai class có thể nằm cùng file Java nhưng nếu đều là Spring bean và A gọi B qua bean được inject thì transaction của B vẫn hoạt động.

#### B dùng `REQUIRES_NEW` rollback thì A có rollback không?

Hai transaction độc lập về commit/rollback, nhưng exception của B vẫn có thể làm A rollback nếu nó tiếp tục truyền ra khỏi A.

**Trường hợp A bắt lỗi của B:** B rollback, A vẫn commit.

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final DashboardStatisticService statisticService;

    @Transactional
    public void createOrder(CreateOrderRequest request) {
        Order order = buildOrder(request);
        orderRepository.save(order);              // transaction A

        try {
            statisticService.incrementOrderCount(); // transaction B
        } catch (RuntimeException ex) {
            log.warn("Cannot update dashboard statistic: {}", ex.getMessage());
        }

        // A tiếp tục và có thể commit đơn hàng
    }
}
```

```java
@Service
@RequiredArgsConstructor
public class DashboardStatisticService {
    private final DashboardStatisticRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void incrementOrderCount() {
        repository.incrementOrderCount();
        throw new IllegalStateException("Statistic database error");
    }
}
```

Luồng thực thi:

```text
A mở transaction A và lưu order
    ↓
Tạm dừng transaction A
    ↓
B mở transaction B mới
    ↓
B lỗi → transaction B rollback
    ↓
A bắt exception của B
    ↓
Khôi phục transaction A → A commit order
```

Thống kê dashboard là dữ liệu phụ, có thể tính lại nên lỗi của nó không nên làm mất đơn hàng chính.

**Trường hợp A không bắt lỗi:** B rollback và exception tiếp tục bay ra khỏi A; proxy của A thấy `RuntimeException` nên A cũng rollback:

```java
@Transactional
public void createOrder(CreateOrderRequest request) {
    orderRepository.save(buildOrder(request));
    statisticService.incrementOrderCount(); // exception truyền ra ngoài
}
```

```text
B rollback độc lập
    ↓
RuntimeException truyền ra khỏi A
    ↓
A rollback vì chính A kết thúc bằng RuntimeException
```

Lưu ý khi dùng `REQUIRES_NEW`:

- B không nhìn thấy dữ liệu chưa commit của A. Không nên để B query một order mà A vừa tạo nhưng chưa commit.
- Tránh để A đang giữ lock trên dòng mà B cũng cần cập nhật, vì A bị tạm dừng nhưng vẫn giữ lock; B có thể chờ chính A và gây deadlock/timeout.
- Chỉ dùng cho nghiệp vụ thật sự độc lập. Nếu A và B phải thành công cùng nhau thì dùng `REQUIRED`, không dùng `REQUIRES_NEW`.

#### B dùng `REQUIRED` rollback thì A cũng rollback

`REQUIRED` là propagation mặc định. Nếu A đã có transaction thì B **tham gia chính transaction đó**, không tạo transaction riêng:

```text
A mở transaction T
    ↓
B dùng lại transaction T
    ↓
B ném RuntimeException ra khỏi method
    ↓
Interceptor của B đánh dấu T là rollback-only
    ↓
A kết thúc → T rollback
```

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    private final PaymentService paymentService;

    @Transactional
    public void createOrder() {
        orderRepository.save(new Order());

        try {
            paymentService.createPayment();
        } catch (RuntimeException ex) {
            log.warn("Payment failed: {}", ex.getMessage());
        }

        // Dù A đã bắt lỗi, transaction chung có thể đã là rollback-only.
        // Khi A cố commit, Spring rollback và ném UnexpectedRollbackException.
    }
}
```

```java
@Service
public class PaymentService {
    @Transactional // Propagation.REQUIRED mặc định
    public void createPayment() {
        paymentRepository.save(new Payment());
        throw new IllegalStateException("Payment gateway error");
    }
}
```

Không nên nói "B gửi cờ lên A" theo nghĩa B có transaction riêng. Chính xác hơn: A và B đang dùng chung một transaction; interceptor của B đặt trạng thái `rollback-only` trên transaction chung. A có bắt exception thì cũng không xóa được trạng thái này.

Các ngoại lệ cần nhớ:

- Mặc định chỉ `RuntimeException` và `Error` kích hoạt rollback. Checked exception không rollback trừ khi cấu hình `rollbackFor`.
- Nếu B bắt và xử lý exception hoàn toàn **bên trong B**, exception không đi qua transactional interceptor nên Spring thường không tự đánh dấu rollback-only; tuy nhiên JPA/database vẫn có thể đánh dấu transaction hỏng với một số lỗi persistence.
- Có thể thay đổi quy tắc bằng `noRollbackFor`, nhưng phải chắc chắn việc commit phần dữ liệu còn lại là an toàn.

---

## 6. Java / Spring nói chung

- **Chỉ bắt exception mình dự đoán được.** `catch (Exception e)` biến lỗi DB thành "token không hợp lệ", rất khó debug.
- **Không `return null`** cho trường hợp lỗi. Ném exception phù hợp.
- **Exception chaining:** khi bắt exception này rồi ném exception khác, luôn truyền exception gốc làm `cause`: `throw new MyException("thông điệp cho client", e)`. Custom exception cần thêm constructor `(String message, Throwable cause)` gọi `super(message, cause)`. Mất `cause` là mất dấu vết khi debug.
- **Tách 2 đối tượng đọc lỗi:** **client** nhận thông điệp chung chung (không lộ chi tiết cho kẻ tấn công); **log** ghi lý do thật (tên class exception + message). Log `ex` làm tham số cuối (`log.warn("...", ex)`) sẽ in cả stack trace và dòng `Caused by:`.
- **Log ở một chỗ** (thường là `GlobalExceptionHandler`), tránh vừa log ở `catch` vừa log ở handler, gây trùng log.
- **Không bao giờ log nguyên chuỗi token** (nó là thông tin đăng nhập). Log `jti`, `userId` hoặc loại lỗi là đủ.
- Các exception của JJWT: `ExpiredJwtException`, `SignatureException`, `MalformedJwtException`, `UnsupportedJwtException` (đều là `JwtException`). Chuỗi rỗng hoặc `null` thì ném `IllegalArgumentException`, **không phải** `JwtException`.
- **Kiểm tra đầu vào trước (guard clause) thay vì bắt exception chung chung.** Ví dụ header `Bearer` rỗng: kiểm tra `isBlank()` rồi coi như request không có token. Đừng `catch (IllegalArgumentException)`, vì đây là exception rất chung: **mọi đoạn code** trong `try` đều có thể ném nó vì một bug không liên quan, và `catch` sẽ âm thầm che bug đó. Chỉ dùng exception cho tình huống **bất thường**, không dùng cho trường hợp đầu vào **dự đoán được**.
- `assert` bị tắt mặc định, không dùng để kiểm tra null.
- SLF4J: `log.warn("lý do: {}", e.getMessage())`, phải có `{}`.
- Hằng số `private static final` dùng ở **mọi chỗ**, không chỉ một nửa.
- `"CONST".equals(value)` để tránh NPE.
- **Spring Boot không tự đọc file `.env`.** Cần plugin EnvFile hoặc khai báo biến trong Run Configuration.
- `@RestControllerAdvice` chỉ bắt exception từ **controller**, không bắt exception từ filter.
- `Ctrl+Alt+O` dọn import thừa, `Ctrl+Alt+L` format code, `Shift+F6` rename an toàn.
- Refactor: xóa hàm cũ trước, rồi **để compiler chỉ ra** những chỗ cần sửa.
- **Bean `OpenAPI` (springdoc) chỉ là tài liệu**, không bảo vệ API. Bảo vệ API là việc của `SecurityFilterChain`. Swagger chỉ biết "endpoint này cần token" để hiện nút **Authorize** và tự gắn header `Authorization: Bearer <token>` khi bấm *Try it out*.
- `SecurityScheme` (trong `components`) = **định nghĩa** kiểu xác thực. `SecurityRequirement` (`addSecurityItem`) = **áp dụng** nó. Đặt ở mức global thì mọi endpoint đều hiện ổ khóa, kể cả `/login` (không sai, nhưng gây hiểu nhầm). Muốn chính xác thì gắn `@SecurityRequirement(name = ...)` ở từng controller cần login.
- `.name(...)` trong `SecurityScheme` chỉ có tác dụng với `Type.APIKEY` (tên header/query). Với `Type.HTTP` nó bị bỏ qua.
- Swagger UI đang `permitAll`: production nên tắt bằng `springdoc.api-docs.enabled=false` và `springdoc.swagger-ui.enabled=false` theo profile, vì nó lộ toàn bộ danh sách API.

### 6.1. Trả HTTP `204 No Content` bằng `ResponseEntity`

```java
@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable Long id) {
    productService.delete(id);
    return ResponseEntity.noContent().build();
}
```

Cách viết tương đương:

```java
return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
```

- Dùng `ResponseEntity<Void>` vì status `204` không có response body.
- Không trả `ApiResponse` kèm status `204`. Nếu muốn trả message hoặc dữ liệu trong body thì dùng `200 OK`.

### 6.2. Vì sao truyền `Throwable` cuối câu log sẽ in stack trace?

```java
log.warn(
        "Unauthorized access: {}",
        ex.getMessage(),
        ex.getCause()
);
```

Câu format chỉ có một `{}`, nên `ex.getMessage()` được dùng cho placeholder. SLF4J thấy tham số cuối còn lại là `Throwable` thì coi nó là exception và in toàn bộ stack trace ở mức `WARN`. Token sai hoặc hết hạn xảy ra nhiều sẽ làm log rất dài.

Nên để `WARN` là một dòng ngắn, còn stack trace đầy đủ ở `DEBUG`:

```java
Throwable cause = ex.getCause();

if (cause != null) {
    log.warn(
            "Unauthorized access: {} (cause: {}: {})",
            ex.getMessage(),
            cause.getClass().getSimpleName(),
            cause.getMessage()
    );
    log.debug("Unauthorized access stack trace", ex);
} else {
    log.warn("Unauthorized access: {}", ex.getMessage());
}
```

Không ghi nguyên chuỗi JWT vào log. Tên exception, message, `jti` hoặc `userId` thường đã đủ để điều tra.

---

## 7. Bảo mật cấu hình và Git

- Secret (JWT key, DB password) **không bao giờ** nằm trong code hoặc `application.yml` được commit.
- `.env` nằm trong `.gitignore`; commit `.env.example` chỉ có tên biến.
- Secret **không có giá trị mặc định**: thiếu biến thì app phải dừng, không tự chạy bằng key ai cũng biết.
- Secret đã lỡ public thì **phải đổi secret mới**, xóa file thôi là không đủ (lịch sử git vẫn còn).
- Commit theo format `[TYPE] Tiêu đề` + **một dòng trống** + gạch đầu dòng. Thiếu dòng trống thì tiêu đề và nội dung bị dính thành một dòng.
- Commit tách theo ý nghĩa: config, migration/entity, refactor, feature, docs. Code chưa xong thì đánh dấu `[WIP]`.
- Làm trên nhánh tính năng (`feat/...`) rồi merge hoặc tạo PR, không commit thẳng vào `main`.

---

## 8. Tư duy senior: những câu tự hỏi

1. *INSERT mà không truyền cột này thì giá trị là gì?*
2. *Query nào chạy nhiều nhất?* → index cột đó.
3. *Trong nghiệp vụ thật, dữ liệu cha có bao giờ bị xóa không?*
4. *Hai request đến **cùng lúc** thì chuyện gì xảy ra?*
5. *Nếu bước này thành công mà bước sau lỗi thì dữ liệu ở trạng thái nào?*
6. *Có use case nào thật sự gọi tới đoạn code này không?* Không có thì xóa.
7. *Client nhận được HTTP status gì trong trường hợp lỗi?* Có đúng không?
8. *Ai cần dữ liệu này?* → người đó tạo ra nó.
9. *Nếu cấu hình bị sai (ví dụ 2 secret giống nhau) thì còn lớp bảo vệ nào không?*
10. *Exception này bay tới đâu, ai bắt nó?*

---

## 9. Câu hỏi phỏng vấn đã trả lời được

- **Lộ token thì thu hồi thế nào?** Access token sống ngắn; refresh token lưu whitelist trong DB theo `jti`; có rotation và reuse detection; logout tất cả thiết bị bằng `user_id`.
- **Vì sao tách key access và refresh?** Để token loại này không dùng được như loại kia; kèm claim `type` làm lớp bảo vệ thứ hai.
- **`@ManyToOne` mặc định fetch kiểu gì?** EAGER, nên luôn đặt LAZY.
- **JPA cascade khác `ON DELETE CASCADE` thế nào?** Xem 3.4.
- **Hard delete hay soft delete cho user e-commerce?** Soft delete, vì phải giữ đơn hàng.
- **`@Transactional` rollback khi nào?** `RuntimeException` (mặc định); checked exception thì không.
- **Làm sao chống race condition khi trừ tồn kho hoặc dùng token?** `UPDATE` có điều kiện, kiểm tra số dòng bị ảnh hưởng; hoặc `@Version` + retry.
- **Vì sao `order_items` lưu snapshot tên/giá?** Để seller đổi giá sau này không làm sai lịch sử đơn.
- **Refresh token rotation, nếu revoke cũ xong mà cấp mới lỗi thì sao?** Transaction rollback cả hai, token cũ vẫn dùng được, user không bị đăng xuất oan.
- **Vì sao find-then-save không an toàn khi có nhiều request đồng thời?** Lỗi check-then-act: cả hai cùng đọc trước khi ai kịp ghi. Dùng `UPDATE ... WHERE` có điều kiện và kiểm tra số dòng.
- **Ghi DB rồi ném exception trong `@Transactional` thì sao?** Rollback mất phần ghi. Giải pháp: `noRollbackFor`, `REQUIRES_NEW` ở bean khác, hoặc không ném exception bên trong transaction.
- **Vì sao `@Transactional` gọi trong cùng class không có tác dụng?** Spring dùng proxy; gọi `this.method()` đi thẳng vào object thật, bỏ qua proxy.
- **Logout có nên báo lỗi khi token đã hết hạn?** Không. Logout nên idempotent: mục tiêu "token không dùng được" đã đạt.

---

## 10. Trạng thái hiện tại và việc tiếp theo

**Refresh token:** Bước 1–4 ✅ (đã commit trên nhánh `feat/refresh-token-auth`, chưa merge, chưa push)

Còn lại:
- [ ] Dùng hằng số trong `parseAccessToken`/`parseRefreshToken` (vẫn đang gõ tay chuỗi)
- [ ] Chạy app và checklist Bruno Bước 3–4
- [ ] Kiểm tra alias `or` trong `OrderItemRepository` (có thể lỗi parse JPQL)
- [x] Bước 5: rotation. L19 đã sửa (JPQL dùng tên entity, truyền `:now` = `Instant.now() - skew` để thống nhất một đồng hồ với JJWT)
- [x] Bước 6: reuse detection. L17, L18, L20 đã sửa (2026-10-11), L17 dùng `noRollbackFor`. Còn: thu hẹp `try` quanh bước parse, chạy kịch bản A → B → A → B trên Bruno
- [x] Access token đã rút xuống 15 phút (`900000` ms)
- [x] Bước 7: logout, logout-all, thu hồi token khi đổi mật khẩu/khóa user (2026-10-12). Còn: chạy kịch bản Bruno, kiểm tra `is_active` và mật khẩu thật sự đổi trong DB
- [ ] Bước 8: job dọn bảng (chỉ xóa token **đã hết hạn**, giữ token đã revoke để còn phát hiện reuse)
- [ ] Bước 9: integration test (Testcontainers), README, Bruno

**Module review:** `[WIP]`, `createReviewItem` chưa lưu và vẫn `return null`.

**Nợ kỹ thuật:** xem `Docx/tech-debt.md`.

---

## 11. Nhật ký

> Mỗi buổi học hoặc làm việc, thêm một mục: ngày, đã làm gì, học được gì, sai gì.

### 2026-09-30: Đánh giá project
- Điểm mạnh: mô hình tồn kho (quantity/reserved/available + log + `@Version`), state machine đơn hàng, snapshot order item, Flyway + `ddl-auto: validate`, check quyền sở hữu trong query (chống IDOR), Strategy Pattern cho payment.
- Điểm yếu lớn: chỉ có test `contextLoads`; secret commit lên GitHub; refresh token dùng được như access token; `/refresh-token` không nằm trong `permitAll`; filter không kiểm tra `isEnabled`; VNPay tự đánh dấu đã thanh toán; tồn kho có 2 nguồn dữ liệu (`stock` và `inventory`); N+1 ở trang danh sách sản phẩm và lịch sử đơn.

### 2026-10-02: Thiết kế refresh token (Bước 1–2)
- Học: whitelist và blacklist; lưu `jti`; quan hệ một chiều; LAZY; JPA cascade và `ON DELETE`; hard delete và soft delete; `@Builder.Default`.
- Sai: L7, L8, L9, L10, L11, L13, L16.

### 2026-10-03: Refactor `JwtTokenProvider` (Bước 3–4)
- Học: parse đã bao gồm validate; 2 tầng hợp lệ; encapsulation key; ném `JwtException` để bên gọi tự quyết; lưu DB trước hay ký trước; `@Transactional` chỉ rollback `RuntimeException`.
- Sai: L3, L5, L6, L12, L14, L15.

### 2026-10-04: Hoàn thành Bước 3–4, commit
- Sai: L1, L2, L4; `assert`; log thiếu `{}`; hằng số dùng nửa vời.
- Đã commit 6 commit theo đúng format trên nhánh `feat/refresh-token-auth`.

### 2026-10-10: Đọc hiểu `SwaggerConfig`
- Học: bean `OpenAPI` chỉ là metadata cho tài liệu, không bảo vệ API; `SecurityScheme` là định nghĩa, `SecurityRequirement` là áp dụng; scheme `bearer` tự thêm tiền tố `Bearer `.
- Sai: mô tả Swagger hướng dẫn nhập `Bearer <token>`, làm header bị lặp tiền tố.

### 2026-10-10: Bước 5–6 (rotation, reuse detection)
- Học: atomicity khi rotation (5.5); lỗi check-then-act và `UPDATE` có điều kiện (5.6); bẫy rollback khi ghi rồi ném exception (5.7); proxy và self-invocation (5.8); logout idempotent (2.8).
- Sai: L17, L18, L19, L20.

### 2026-10-11: Sửa L17–L20
- L19: JPQL dùng tên entity; truyền `:now = Instant.now() - 30s` cho khớp `clockSkewSeconds(30)` của JJWT, để chỉ một đồng hồ quyết định.
- L18, L20: điều kiện hết hạn đúng chiều; mọi nhánh `rowChange == 0` đều từ chối.
- L17: chọn `noRollbackFor`. Học thêm: phải rà từng chỗ ném exception, và cẩn thận khi `catch` đổi loại exception (mục 5.7).

### 2026-10-12: Bước 7 (logout, logout-all), exception chaining
- Học: `@Modifying` cần transaction; `clearAutomatically` làm mất thay đổi chưa flush (5.9); GET phải an toàn (2.11); exception chaining và guard clause (mục 6).
- Sai: L21, L22, L23, L24.
- Ôn lại: `isEmpty()`/`isBlank()`/`StringUtils.hasText()`; cách `CustomUserDetails` trở thành principal; `Authentication.getPrincipal()` và `getName()`; trả `204 No Content`; quy tắc SLF4J khi tham số cuối là `Throwable`; thứ tự `flush → modifying query → clear`.
