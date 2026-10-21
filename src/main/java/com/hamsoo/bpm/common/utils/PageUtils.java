package com.hamsoo.bpm.common.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

public class PageUtils {

    public static final int MAX_PAGE_SIZE = 200;

    public static int validatePagesSize(int size) {
        if (size > MAX_PAGE_SIZE) {
            size = MAX_PAGE_SIZE;
        }
        return size;
    }

    public static Pageable getInstance(int page, int size) {
        size = validatePagesSize(size);
        return PageRequest.of(page, size);
    }

    public static Pageable getInstance(int page, int size, String orderBy, Sort.Direction direction) {
        size = validatePagesSize(size);
        Pageable pageable;
        if (direction != null && StringUtils.hasText(orderBy)) {
            Sort sort = Sort.by(direction, orderBy);
            pageable = PageRequest.of(page, size, sort);
        } else {
            pageable = PageRequest.of(page, size);
        }
        return pageable;
    }

    public static Pageable getInstance() {
        return PageRequest.of(0, MAX_PAGE_SIZE);
    }
}
