package com.example.photoprintapplication.models;

public class Photo {
    private Long id;
    private String filename;
    private String description;
    private Format format;
    private Long orderId;

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

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
}