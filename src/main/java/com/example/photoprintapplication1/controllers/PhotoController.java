package com.example.photoprintapplication.controllers;

import com.example.photoprintapplication.models.Photo;
import com.example.photoprintapplication.models.Format;
import com.example.photoprintapplication.models.User;
import com.example.photoprintapplication.repository.PhotoRepository;
import com.example.photoprintapplication.repository.FormatRepository;
import com.example.photoprintapplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private FormatRepository formatRepository;

    @Autowired
    private UserRepository userRepository;


    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public Photo create(@RequestBody Photo photo) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // Проверяем, принадлежит ли заказ пользователю
        if (photo.getOrder() != null && photo.getOrder().getId() != null) {
            Photo existingPhoto = photoRepository.findById(photo.getId()).orElse(null);
            if (existingPhoto != null &&
                    !existingPhoto.getOrder().getUser().getUsername().equals(username)) {
                throw new AccessDeniedException("You can only add photos to your own orders");
            }
        }

        if (photo.getFormat() != null && photo.getFormat().getId() != null) {
            Format format = formatRepository.findById(photo.getFormat().getId())
                    .orElse(null);
            photo.setFormat(format);
        }
        return photoRepository.save(photo);
    }


    @GetMapping("/my-photos")
    @PreAuthorize("hasRole('USER')")
    public List<Photo> getMyPhotos() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return photoRepository.findByOrderUserId(currentUser.getId());
    }


    @GetMapping("/my-photos/{id}")
    @PreAuthorize("hasRole('USER')")
    public Photo getMyPhoto(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Photo not found"));

        // Проверяем, принадлежит ли фото пользователю
        if (photo.getOrder() == null ||
                !photo.getOrder().getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("You can only view your own photos");
        }

        return photo;
    }


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Photo> getAllPhotos() {
        return photoRepository.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Photo getPhoto(@PathVariable Long id) {
        return photoRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Photo update(@PathVariable Long id, @RequestBody Photo photo) {
        Photo exist = photoRepository.findById(id).orElse(null);
        if (exist == null) return null;

        exist.setFilename(photo.getFilename());
        exist.setDescription(photo.getDescription());

        if (photo.getFormat() != null && photo.getFormat().getId() != null) {
            Format format = formatRepository.findById(photo.getFormat().getId()).orElse(null);
            exist.setFormat(format);
        }

        return photoRepository.save(exist);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        photoRepository.deleteById(id);
        return "ok";
    }
}