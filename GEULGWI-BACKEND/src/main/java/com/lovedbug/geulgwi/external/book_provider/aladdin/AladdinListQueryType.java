package com.lovedbug.geulgwi.external.book_provider.aladdin;

public enum AladdinListQueryType {

    ITEM_NEW_ALL("ItemNewAll", "신간 전체 리스트"),
    ITEM_NEW_SPECIAL("ItemNewSpecial", "주목할 만한 신간 리스트"),
    ITEM_EDITOR_CHOICE("ItemEditorChoice", "편집자 추천 리스트 (카테고리: 국내도서/음반/외서만)"),
    BESTSELLER("Bestseller", "베스트셀러"),
    BLOG_BEST("BlogBest", "블로거 베스트셀러 (국내도서만)");

    private final String value;
    private final String description;

    AladdinListQueryType(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}

