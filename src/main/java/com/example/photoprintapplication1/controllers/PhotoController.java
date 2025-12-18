package com.example.photoprintapplication1.controllers;

import com.example.photoprintapplication1.models.Photo;
import com.example.photoprintapplication1.models.Format;
import com.example.photoprintapplication1.models.Order;
import com.example.photoprintapplication1.repository.PhotoRepository;
import com.example.photoprintapplication1.repository.FormatRepository;
import com.example.photoprintapplication1.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private FormatRepository formatRepository;

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")  // Разрешить USER/ADMIN, если нужно
    public Photo create(@RequestBody Map<String, Object> request) {
        String filename = (String) request.get("filename");
        String description = (String) request.get("description");
        Long formatId = request.containsKey("formatId") ? Long.parseLong(request.get("formatId").toString()) : null;
        Long orderId = request.containsKey("orderId") ? Long.parseLong(request.get("orderId").toString()) : null;

        if (formatId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "formatId is required");
        }

        Format format = formatRepository.findById(formatId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Format not found: " + formatId));

        Photo photo = new Photo();
        photo.setFilename(filename != null ? filename : "default.jpg");
        photo.setDescription(description != null ? description : "");
        photo.setFormat(format);

        if (orderId != null) {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found: " + orderId));
            photo.setOrder(order);
        }

        return photoRepository.save(photo);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<Photo> all() {
        return photoRepository.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Photo get(@PathVariable Long id) {
        return photoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Photo not found: " + id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Photo update(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Photo not found: " + id));

        if (request.containsKey("filename")) {
            photo.setFilename((String) request.get("filename"));
        }
        if (request.containsKey("description")) {
            photo.setDescription((String) request.get("description"));
        }
        if (request.containsKey("formatId")) {
            Long formatId = Long.parseLong(request.get("formatId").toString());
            Format format = formatRepository.findById(formatId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Format not found: " + formatId));
            photo.setFormat(format);
        }
        if (request.containsKey("orderId")) {
            Long orderId = Long.parseLong(request.get("orderId").toString());
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found: " + orderId));
            photo.setOrder(order);
        }

        return photoRepository.save(photo);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        photoRepository.deleteById(id);
        return "ok";
    }
}