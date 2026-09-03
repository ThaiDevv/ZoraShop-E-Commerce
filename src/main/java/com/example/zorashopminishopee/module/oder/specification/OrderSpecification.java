package com.example.zorashopminishopee.module.oder.specification;

import com.example.zorashopminishopee.module.oder.entity.Order;
import com.example.zorashopminishopee.module.oder.enums.StatusType;
import com.example.zorashopminishopee.module.users.entity.Shops;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecification {
    public static Specification<Order> hasUserEmail(String email) {
        return (root, query, cb)
                -> cb.equal(root.get("user").get("email"), email);
    }

    public static Specification<Order> hasStatus(StatusType status) {
        return (root, query, cb)
                -> (status == null) ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Order> hasShop(Shops shop) {
        return (root, query, cb)
                -> (shop == null) ? null : cb.equal(root.get("shop"), shop);
    }
}
