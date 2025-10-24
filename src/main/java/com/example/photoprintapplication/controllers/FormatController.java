package com.example.photoprintapplication.controllers;

import com.example.photoprintapplication.models.Format;
import com.example.photoprintapplication.repository.PhotoPrintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/formats")
public class FormatController {
    @Autowired
    private PhotoPrintRepository repo;

    @PostMapping
    public Format create(@RequestBody Format format) {
        return repo.save(format);
    }

    @GetMapping
    public List<Format> all() {
        return repo.findAllFormats();
    }

    @GetMapping("/{id}")
    public Format get(@PathVariable Long id) {
        return repo.findFormatById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Format update(@PathVariable Long id, @RequestBody Format format) {
        Format exist = repo.findFormatById(id).orElse(null);
        if (exist == null) return null;

        exist.setName(format.getName());
        exist.setDescription(format.getDescription());
        exist.setPrice(format.getPrice());

        return repo.save(exist);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        repo.deleteFormatById(id);
        return "ok";
    }
}