package com.example.demo.model;

import jakarta.persistence.*;

@Entity
public class PendingBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private String genre;
    private double price;

    private Long dealerId;     // for dealer uploads
    private Long customerId;   // for customer donations

    private String description;
    private String status = "PENDING";

    public PendingBook() {}

    public PendingBook(String title, String author, String genre, double price,
                       Long dealerId, Long customerId, String description) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.price = price;
        this.dealerId = dealerId;
        this.customerId = customerId;
        this.description = description;
        this.status = "PENDING";
    }

    // ✅ Getters & Setters

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }

    public void setAuthor(String author) { this.author = author; }

    public String getGenre() { return genre; }

    public void setGenre(String genre) { this.genre = genre; }

    public double getPrice() { return price; }

    public void setPrice(double price) { this.price = price; }

    public Long getDealerId() { return dealerId; }

    public void setDealerId(Long dealerId) { this.dealerId = dealerId; }

    public Long getCustomerId() { return customerId; }

    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }
}
