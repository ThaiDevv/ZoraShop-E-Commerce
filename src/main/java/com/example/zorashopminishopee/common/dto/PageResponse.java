package com.example.zorashopminishopee.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResponse<T> {
    private List<T> items;
    private int pageNo;
    private int pageSize;
    private Long totalElements;
    private int totalPages;
    private Boolean last;
    public static <T> PageResponse<T> fromPage (Page<T> page) {
        return PageResponse.<T>builder()
                .items(page.getContent())
                .pageNo(page.getNumber() + 1)
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
    public static <E, T> PageResponse<T> fromPage (Page<E> page, List<T> items) {
        return PageResponse.<T>builder()
                .items(items)
                .pageNo(page.getNumber() + 1)
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

}
