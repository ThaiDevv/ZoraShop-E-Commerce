# Project Guidelines & Code Review Rules

## Autonomous File Analysis & Workspace Inspection
- Proactively inspect and read any workspace source files, DTOs, entities, controllers, repositories, migrations, and documentation needed to complete analysis or reviews without asking for confirmation.

## Code Review Standards (Enterprise & Production Readiness)

Whenever reviewing code or answering user questions about implementations:
1. **OOP & SOLID Principles Audit**:
   - Check Single Responsibility Principle (SRP) across Services, Controllers, and Repositories.
   - Enforce proper Aggregate Root boundaries (e.g. `Cart` managing `CartItem`).
   - Prevent circular dependencies and improper bean injections.

2. **Database Performance & N+1 Query Prevention**:
   - Explicitly audit Hibernate/JPA relationships (`@OneToMany`, `@ManyToOne`, `@OneToOne`).
   - Detect potential N+1 query traps during entity loading or mapping loops.
   - Recommend `@EntityGraph`, `JOIN FETCH`, or batch fetching (`@BatchSize`) where appropriate.

3. **Concurrency, Security & Reliability (Production Checklist)**:
   - Check Optimistic/Pessimistic Locking (`@Version`) on critical domain data (e.g. `Inventory`, `Voucher`).
   - Verify proper `@Transactional` boundaries, propagation, and read-only flags (`readOnly = true`).
   - Audit Ownership Authorization (`ForbiddenException`) for tenant/shop isolation.
   - Enforce Input Validation (`@Valid`, `@NotNull`, `@NotBlank`) and safe Null Pointer checks.

4. **DTO & Domain Business Completeness Audit**:
   - Audit all Request/Response DTOs for business domain completeness (e.g., verifying price, stock, originalPrice, SKU/ids are present for UI needs).
   - Ensure Request DTOs have strict Bean Validation annotations (`@NotNull`, `@NotBlank`, `@Min`, `@Pattern`).
   - Check DTO separation (Request DTOs vs Response DTOs) to prevent exposing sensitive entity internals.
