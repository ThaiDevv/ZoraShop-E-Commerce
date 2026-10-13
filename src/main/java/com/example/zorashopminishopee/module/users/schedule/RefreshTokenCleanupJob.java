package com.example.zorashopminishopee.module.users.schedule;

import com.example.zorashopminishopee.module.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupJob {
    private final UserService userService;
    @Scheduled(
            cron = "${jobs.cleanup.cron}",
            zone = "${jobs.time-zone}"
    )
    public void cleanup() {
        try {
            userService.deleteExpiredToken();
        }catch (RuntimeException e) {
            log.error("Error while cleaning up expired refresh tokens", e);
        }
    }
}
