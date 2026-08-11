package com.example.zorashopminishopee;

import org.springframework.boot.SpringApplication;

public class TestZoraShopMiniShopeeECommercePlatformApplication {

    public static void main(String[] args) {
        SpringApplication.from(ZoraShopMiniShopee::main).with(TestcontainersConfiguration.class).run(args);
    }

}
