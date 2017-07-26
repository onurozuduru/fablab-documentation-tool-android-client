package fi.oulu.fablab.myapplication1.models;
/*
Model class for Page Object.
JSON Object:
    {
        "per_page": Integer,
        "total": Integer,
        "page": Integer,
        "pages": Integer,
        "items": List of <T>
    }
*/

import java.util.List;

public class Page<T> {
    private int per_page;
    private int total;
    private int page;
    private int pages;
    private List<T> items;

    public Page(int per_page, int total, int page, int pages, List<T> items) {
        this.per_page = per_page;
        this.total = total;
        this.page = page;
        this.pages = pages;
        this.items = items;
    }

    public int getPer_page() {
        return per_page;
    }

    public void setPer_page(int per_page) {
        this.per_page = per_page;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
