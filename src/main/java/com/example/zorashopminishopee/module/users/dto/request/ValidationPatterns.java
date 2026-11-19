package com.example.zorashopminishopee.module.users.dto.request;

public final class ValidationPatterns {
    /** Vietnamese phone number: 0xxxxxxxxx or +84xxxxxxxxx (9-10 digits after the prefix). */
    public static final String PHONE = "^(0|\\+84)[0-9]{9,10}$";

    private ValidationPatterns() {
    }
}
