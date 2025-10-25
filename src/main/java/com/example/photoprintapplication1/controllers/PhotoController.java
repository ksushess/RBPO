package com.example.photoprintapplication.controllers;

import com.example.photoprintapplication.models.Photo;
import com.example.photoprintapplication.models.Format;
import com.example.photoprintapplication.repository.PhotoPrintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {
    @Autowired
    private PhotoPrintRepository repo;

    @PostMapping
    public Photo create(@RequestBody Photo photo) {
        // Если передан формат с ID, находим его
        if (photo.getFormat() != null && photo.getFormat().getId() != null) {
            Format format = repo.findFormatById(photo.getFormat().getId()).orElse(null);
            photo.setFormat(format);
        }
        return repo.save(photo);
    }

    @GetMapping
    public List<Photo> all() {
        return repo.findAllPhotos();
    }

    @GetMapping("/{id}")
    public Photo get(@PathVariable Long id) {
        return repo.findPhotoById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Photo update(@PathVariable Long id, @RequestBody Photo photo) {
        Photo exist = repo.findPhotoById(id).orElse(null);
        if (exist == null) return null;

        exist.setFilename(photo.getFilename());
        exist.setDescription(photo.getDescription());

        // Обновляем формат если передан
        if (photo.getFormat() != null && photo.getFormat().getId() != null) {
            Format format = repo.findFormatById(photo.getFormat().getId()).orElse(null);
            exist.setFormat(format);
        }

        return repo.save(exist);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        repo.deletePhotoById(id);
        return "ok";
    }
}