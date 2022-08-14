package com.example.bookborrow.entity;

public class PublishedBooks {
    private Book book;
    private User user;

    public Book getBook() {
        return book;
    }

    public User getUser() {
        return user;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
