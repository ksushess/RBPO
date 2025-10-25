package com.example.photoprintapplication.models;

import java.math.BigDecimal;

public class Format {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;

    public Format() {}
    public Format(String name, String description, BigDecimal price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }
    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}