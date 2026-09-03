package com.example.zorashopminishopee.module.oder.enums;

public enum StatusType {
    PENDING,
    CONFIRMED,
    SHIPPING,
    DELIVERED,
    CANCELLED,
    REFUNDED;

    public boolean canTransitionTo(StatusType next) {
        if (next == null) return false;
        return switch (this) {
            case PENDING -> next == CONFIRMED || next == CANCELLED;
            case CONFIRMED -> next == SHIPPING || next == CANCELLED;
            case SHIPPING -> next == DELIVERED;
            case DELIVERED -> next == REFUNDED;
            default -> false;
        };
    }
}
