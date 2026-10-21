package com.hamsoo.bpm.model.dto.search;

import java.util.List;

public class SearchDto {

    private List<?> content;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;


    public List<?> getContent() {
        return content;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public boolean isLast() {
        return this.totalElements <= ((currentPage + 1) * pageSize);
    }

    private SearchDto(List<?> content, long totalElements, int totalPages, int currentPage, int pageSize) {
        this.content = content;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    public static class Builder {
        private List<?> content;
        private long totalElements;
        private int totalPages;
        private int currentPage;
        private int pageSize;

        public Builder content(List<?> content) {
            this.content = content;
            return this;
        }

        public Builder totalElements(long totalElements) {
            this.totalElements = totalElements;
            return this;
        }

        public Builder totalPages(int totalPages) {
            this.totalPages = totalPages;
            return this;
        }

        public Builder currentPage(int currentPage) {
            this.currentPage = currentPage;
            return this;
        }

        public Builder pageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public SearchDto build() {
            return new SearchDto(content, totalElements, totalPages, currentPage, pageSize);
        }
    }
}
