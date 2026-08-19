<p align="center">
  <img src="https://api.dicebear.com/7.x/shapes/svg?seed=zorashop&backgroundColor=6366f1&size=120" alt="ZoraShop Logo" width="120" height="120" />
</p>

<h1 align="center">🛒 ZoraShop — Mini Shopee E-Commerce Platform</h1>

<p align="center">
  <strong>A production-ready, modular e-commerce backend built with Spring Boot 4 & Java 17</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-4.1.0-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" />
  <img src="https://img.shields.io/badge/PostgreSQL-16-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL" />
  <img src="https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker" />
  <img src="https://img.shields.io/badge/Flyway-Migration-CC0200?style=for-the-badge&logo=flyway&logoColor=white" alt="Flyway" />
  <img src="https://img.shields.io/badge/JWT-Auth-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white" alt="JWT" />
</p>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Architecture](#-architecture)
- [Tech Stack](#-tech-stack)
- [Getting Started](#-getting-started)
- [Project Structure](#-project-structure)
- [API Reference](#-api-reference)
- [Database Schema](#-database-schema)
- [API Testing (Bruno)](#-api-testing-bruno)
- [Environment Variables](#-environment-variables)
- [Contributing](#-contributing)
- [License](#-license)

---

## 🌟 Overview

**ZoraShop** is a full-featured e-commerce backend platform inspired by Shopee, designed with a clean modular architecture. It supports multi-role authentication (Admin / Seller / Buyer), a hierarchical 3-level category system, shop management, and address book — all powered by a RESTful API with JWT-based security.

### ✅ Key Features

| Feature | Status | Description |
|---------|--------|-------------|
| **Authentication** | ✅ Done | Register, Login with JWT Access / Refresh Token |
| **User Management** | ✅ Done | Profile CRUD, Admin user listing with pagination |
| **Address Book** | ✅ Done | Multi-address per user, set default address |
| **Shop Management** | ✅ Done | Seller shop profile CRUD |
| **Category Tree** | ✅ Done | 3-level hierarchical category with O(N) tree building |
| **Admin Panel APIs** | ✅ Done | User management, category CRUD with role-based access |
| **API Documentation** | ✅ Done | Swagger UI / OpenAPI 3.0 |
| **Database Migration** | ✅ Done | Flyway versioned migrations |

---

## 🏗 Architecture

```
┌──────────────────────────────────────────────────────────┐
│                      Client (Bruno / Frontend)           │
└───────────────────────────┬──────────────────────────────┘
                            │  HTTP / REST
┌───────────────────────────▼──────────────────────────────┐
│                   Spring Security Filter Chain            │
│            ┌──────────────────────────────┐               │
│            │   JWT Authentication Filter  │               │
│            └──────────────┬───────────────┘               │
│                           ▼                               │
│  ┌─────────────────────────────────────────────────────┐  │
│  │                    Controllers                       │  │
│  │  AuthController · UserController · AdminController   │  │
│  │  ShopController · AddressController                  │  │
│  │  CategoryController · AdminCategoryController        │  │
│  └────────────────────────┬────────────────────────────┘  │
│                           ▼                               │
│  ┌─────────────────────────────────────────────────────┐  │
│  │                  Service Layer                       │  │
│  │   UserService · CategoryService · ShopService ...    │  │
│  └────────────────────────┬────────────────────────────┘  │
│                           ▼                               │
│  ┌─────────────────────────────────────────────────────┐  │
│  │              Repository Layer (JPA)                  │  │
│  │   UserRepository · CategoryRepository · ...          │  │
│  └────────────────────────┬────────────────────────────┘  │
│                           ▼                               │
│  ┌─────────────────────────────────────────────────────┐  │
│  │        PostgreSQL 16 (Docker) + Flyway Migrations    │  │
│  └─────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────┘
```

---

## 🛠 Tech Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| **Runtime** | Java (JBR) | 17.0.14 |
| **Framework** | Spring Boot | 4.1.0 |
| **Security** | Spring Security + JWT (jjwt) | 0.12.5 |
| **Database** | PostgreSQL | 16 (Alpine) |
| **ORM** | Spring Data JPA / Hibernate | — |
| **Migration** | Flyway | — |
| **API Docs** | SpringDoc OpenAPI (Swagger UI) | 2.8.5 |
| **Mapping** | MapStruct | 1.5.5 |
| **Object Storage** | MinIO SDK | 8.5.7 |
| **Build** | Maven Wrapper | 3.9.16 |
| **Containerization** | Docker Compose | 3.8 |
| **API Testing** | Bruno | — |

---

## 🚀 Getting Started

### Prerequisites

- **Java 17** or higher
- **Docker** & **Docker Compose**
- **Maven 3.9+** (or use the included `./mvnw` wrapper)

### 1. Clone the repository

```bash
git clone https://github.com/your-username/ZoraShop-MiniShopeeE.git
cd ZoraShop-MiniShopeeE
```

### 2. Start the database

```bash
docker compose up -d
```

This spins up a PostgreSQL 16 container:
- **Host:** `localhost:5432`
- **Database:** `zorashop_db`
- **Username:** `zorashop_user`
- **Password:** `123456`

### 3. Run the application

```bash
./mvnw spring-boot:run
```

The server starts at: **http://localhost:8080**

### 4. Access Swagger UI

Open your browser and navigate to:

```
http://localhost:8080/swagger-ui.html
```

### 5. (Optional) Load mock data manually

If Flyway migrations are already applied and you want to re-seed:

```bash
docker exec -i zorashop-postgres psql -U zorashop_user -d zorashop_db < mock_data.sql
```

---

## 📁 Project Structure

```
ZoraShop-MiniShopeeE/
├── bruno/                              # API testing collections (Bruno)
│   ├── Admin/                          #   Admin management tests
│   ├── Auth/                           #   Authentication tests
│   ├── Category/                       #   Category CRUD tests
│   ├── Address/                        #   Address management tests
│   ├── Shop/                           #   Shop management tests
│   ├── User/                           #   User profile tests
│   └── environments/                   #   Environment configs
├── src/main/
│   ├── java/.../zorashopminishopee/
│   │   ├── common/                     # Shared components
│   │   │   ├── base/                   #   Base entities (auditing)
│   │   │   ├── dto/                    #   ApiResponse, PageResponse
│   │   │   └── exception/              #   Global exception handler
│   │   ├── config/                     # App configuration
│   │   │   ├── SecurityConfig.java     #   Spring Security + JWT setup
│   │   │   └── SwaggerConfig.java      #   OpenAPI 3.0 config
│   │   ├── security/                   # Security infrastructure
│   │   │   ├── JwtTokenProvider.java   #   JWT token generation & validation
│   │   │   ├── JwtAuthenticationFilter.java
│   │   │   ├── CustomUserDetails.java
│   │   │   └── CustomUserDetailsService.java
│   │   └── module/                     # Feature modules
│   │       ├── users/                  #   Users, Auth, Shop, Address
│   │       │   ├── controller/
│   │       │   ├── dto/
│   │       │   ├── entity/
│   │       │   ├── enums/
│   │       │   ├── repository/
│   │       │   └── service/
│   │       └── catagory/               #   Category module
│   │           ├── controller/
│   │           ├── dto/
│   │           ├── entity/
│   │           ├── repository/
│   │           └── service/
│   └── resources/
│       ├── application.yml             # App configuration
│       └── db/migration/               # Flyway migrations
│           ├── V1__create_users.sql
│           ├── V2__create_address.sql
│           ├── V3__create_shops.sql
│           ├── V4__insert_mock_data.sql
│           └── V5__create_category.sql
├── docker-compose.yaml                 # PostgreSQL container
├── mock_data.sql                       # Full mock data script
├── pom.xml                             # Maven dependencies
└── README.md
```

---

## 📡 API Reference

### 🔐 Authentication

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/api/v1/auth/register` | Register a new account | ❌ Public |
| `POST` | `/api/v1/auth/login` | Login & get JWT tokens | ❌ Public |

### 👤 User Profile

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/v1/users/me` | Get current user profile | 🔒 Bearer |
| `PUT` | `/api/v1/users/me` | Update user profile | 🔒 Bearer |

### 📍 Address Management

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/v1/addresses` | List all addresses | 🔒 Bearer |
| `POST` | `/api/v1/addresses` | Create new address | 🔒 Bearer |
| `PUT` | `/api/v1/addresses/{id}` | Update address | 🔒 Bearer |
| `DELETE` | `/api/v1/addresses/{id}` | Delete address | 🔒 Bearer |
| `PATCH` | `/api/v1/addresses/{id}/default` | Set default address | 🔒 Bearer |

### 🏪 Shop Management

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/v1/shops/me` | Get my shop profile | 🔒 Seller |
| `POST` | `/api/v1/shops` | Create shop | 🔒 Seller |
| `PUT` | `/api/v1/shops/me` | Update shop profile | 🔒 Seller |

### 📂 Categories (Public)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/v1/categories` | Get full category tree (3 levels) | ❌ Public |

### 🛡️ Admin — User Management

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/v1/admin/users?page=0&size=10` | List users (paginated) | 🔒 Admin |
| `PATCH` | `/api/v1/admin/users/{email}/active` | Toggle user active status | 🔒 Admin |

### 🛡️ Admin — Category Management

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/api/v1/admin/categories` | Create category | 🔒 Admin |
| `PUT` | `/api/v1/admin/categories/{id}` | Update category | 🔒 Admin |
| `DELETE` | `/api/v1/admin/categories/{id}` | Delete category | 🔒 Admin |

### 📦 Unified API Response Format

All endpoints return a consistent response structure:

```json
{
  "success": true,
  "message": "Success",
  "data": { ... }
}
```

Error responses:

```json
{
  "success": false,
  "message": "Category slug already exists",
  "data": null
}
```

---

## 🗄 Database Schema

```
┌──────────────┐       ┌──────────────┐       ┌──────────────┐
│    users     │       │    shops     │       │   address    │
├──────────────┤       ├──────────────┤       ├──────────────┤
│ id (PK)      │◄──┐   │ id (PK)      │       │ id (PK)      │
│ email        │   │   │ seller_id(FK)│───┐   │ user_id (FK) │───┐
│ password     │   │   │ name         │   │   │ full_name    │   │
│ full_name    │   │   │ description  │   │   │ phone        │   │
│ phone        │   │   │ logo_url     │   │   │ street       │   │
│ avatar_url   │   │   │ banner_url   │   │   │ ward         │   │
│ role         │   │   │ rating       │   │   │ district     │   │
│ is_active    │   │   │ is_active    │   │   │ city         │   │
│ created_at   │   └───│──────────────│───┘   │ is_default   │   │
│ updated_at   │       └──────────────┘       └──────────────┘   │
└──────────────┘                                                  │
       ▲                                                          │
       └──────────────────────────────────────────────────────────┘

┌───────────────────┐
│    categories     │
├───────────────────┤
│ id (PK)           │
│ parent_id (FK)────│──► self-referencing (3 levels max)
│ name              │
│ slug (UNIQUE)     │
│ icon_url          │
│ level             │
│ sort_order        │
│ is_active         │
│ created_date      │
│ last_modified_date│
└───────────────────┘
```

### Flyway Migration History

| Version | Description |
|---------|-------------|
| `V1` | Create `users` table |
| `V2` | Create `address` table |
| `V3` | Create `shops` table |
| `V4` | Insert mock data (users, shops, addresses) |
| `V5` | Create `categories` table + insert 3-level mock data |

---

## 🧪 API Testing (Bruno)

This project includes a complete [Bruno](https://www.usebruno.com/) collection for API testing.

### Setup

1. Install [Bruno](https://www.usebruno.com/downloads)
2. Open Bruno → **Open Collection** → Select the `bruno/` folder
3. Navigate to **Environments** → Select `local`
4. Run tests in order: **Auth → User → Address → Shop → Category → Admin**

### Collections

```
bruno/
├── Auth/                    # Login & Register
├── User/                    # User profile APIs
├── Address/                 # Address CRUD
├── Shop/                    # Shop management
├── Category/                # Category tree + CRUD
│   ├── 1. Get Category Tree.bru
│   ├── 2. Create Category (Admin).bru
│   ├── 3. Update Category (Admin).bru
│   └── 4. Delete Category (Admin).bru
├── Admin/                   # Admin management
├── environments/            # Environment variables
└── bruno.json               # Collection config
```

### Default Test Accounts

| Role | Email | Password |
|------|-------|----------|
| Admin | `admin@zorashop.com` | `Password123@` |
| Seller | `seller1@zorashop.com` | `Password123@` |
| Buyer | `buyer1@zorashop.com` | `Password123@` |

---

## ⚙️ Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/zorashop_db` | Database connection URL |
| `SPRING_DATASOURCE_USERNAME` | `zorashop_user` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | `123456` | Database password |
| `JWT_SECRET` | *(configured in application.yml)* | JWT signing secret |
| `JWT_ACCESS_EXPIRATION` | `86400000` (24h) | Access token TTL in ms |
| `JWT_REFRESH_EXPIRATION` | `604800000` (7d) | Refresh token TTL in ms |

---

## 🤝 Contributing

1. **Fork** the repository
2. **Create** your feature branch: `git checkout -b feature/amazing-feature`
3. **Commit** with convention: `[FEAT] Add amazing feature`
4. **Push** to the branch: `git push origin feature/amazing-feature`
5. **Open** a Pull Request

### Commit Convention

| Prefix | Usage |
|--------|-------|
| `[FEAT]` | New feature or functionality |
| `[FIX]` | Bug fix |
| `[CHORE]` | Maintenance, refactoring, dependencies |
| `[DOCS]` | Documentation updates |
| `[TEST]` | Adding or updating tests |

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

<p align="center">
  <strong>Built with ❤️ by <a href="https://github.com/thaidevvv">Tran Van Thai</a></strong>
</p>
