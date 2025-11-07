package com.example.photoprintapplication.controllers;

import com.example.photoprintapplication.models.Photo;
import com.example.photoprintapplication.models.Format;
import com.example.photoprintapplication.repository.PhotoRepository;
import com.example.photoprintapplication.repository.FormatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {
    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private FormatRepository formatRepository;

    @PostMapping
    public Photo create(@RequestBody Photo photo) {
        // Если передан формат с ID, находим его
        if (photo.getFormat() != null && photo.getFormat().getId() != null) {
            Format format = formatRepository.findById(photo.getFormat().getId()).orElse(null);
            photo.setFormat(format);
        }
        return photoRepository.save(photo);
    }

    @GetMapping
    public List<Photo> all() {
        return photoRepository.findAll();
    }

    @GetMapping("/{id}")
    public Photo get(@PathVariable Long id) {
        return photoRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Photo update(@PathVariable Long id, @RequestBody Photo photo) {
        Photo exist = photoRepository.findById(id).orElse(null);
        if (exist == null) return null;

        exist.setFilename(photo.getFilename());
        exist.setDescription(photo.getDescription());

        // Обновляем формат если передан
        if (photo.getFormat() != null && photo.getFormat().getId() != null){
            Format format = formatRepository.findById(photo.getFormat().getId()).orElse(null);
            exist.setFormat(format);
        }

        return photoRepository.save(exist);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        photoRepository.deleteById(id);
        return "ok";
    }
}