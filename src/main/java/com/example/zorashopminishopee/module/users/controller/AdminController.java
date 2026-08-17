package com.example.zorashopminishopee.module.users.controller;


import com.example.zorashopminishopee.common.dto.ApiResponse;
import com.example.zorashopminishopee.common.dto.PageResponse;
import com.example.zorashopminishopee.module.users.dto.response.UserResponse;
import com.example.zorashopminishopee.module.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                                               @RequestParam(defaultValue = "10") int size) {
        Page<UserResponse> users = userService.getAllUsers(page, size);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromPage(users)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{email}/active")
    public ResponseEntity<ApiResponse<UserResponse>> changeActive(@PathVariable String email) {
        UserResponse users = userService.changeActive(email);
        return ResponseEntity.ok(ApiResponse.success(users));
    }
}
