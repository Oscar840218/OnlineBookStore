package com.oscar.bookstore.model;

import com.fasterxml.jackson.annotation.*;

import javax.persistence.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "type")
    private String type;

    @Column(name="publish_date")
    private String date;

    @Column(name = "author")
    private String author;

    @Column(name = "price")
    private int price;

    @Column(name = "book_image")
    private byte[] bookPic;

    @Column(name = "description")
    private String description;

    @Column(name = "upload_user_id")
    private Integer uploadUserId;

    @Column(name = "status")
    private String status;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH},
            fetch = FetchType.LAZY)

    @JoinTable(
            name = "customer_book",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "customer_id")
    )
    private List<Customer> customers;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinColumn(name = "book_id")
    private List<Review> reviews;

    public Book() { }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getUploadUserId() {
        return uploadUserId;
    }

    public void setUploadUserId(Integer uploadUserId) {
        this.uploadUserId = uploadUserId;
    }

    public byte[] getBookPic() {
        return bookPic;
    }

    public void setBookPic(byte[] bookPic) {
        this.bookPic = bookPic;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", date='" + date + '\'' +
                ", author='" + author + '\'' +
                ", price=" + price +
                ", bookPic=" + Arrays.toString(bookPic) +
                ", description='" + description + '\'' +
                ", uploadUserId=" + uploadUserId +
                ", status='" + status + '\'' +
                ", customers=" + customers +
                ", reviews=" + reviews +
                '}';
    }
}
