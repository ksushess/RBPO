package com.example.photoprintapplication.models;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "photos")
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "format_id")
    @JsonIgnore
    private Format format;

    public Photo() {}
    public Photo(String filename, String description, Format format) {
        this.filename = filename;
        this.description = description;
        this.format = format;
    }
    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Format getFormat() { return format; }
    public void setFormat(Format format) { this.format = format; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
}