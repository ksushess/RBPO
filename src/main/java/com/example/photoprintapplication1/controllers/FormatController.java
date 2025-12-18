package com.example.photoprintapplication1.controllers;

import com.example.photoprintapplication1.models.Format;
import com.example.photoprintapplication1.repository.FormatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/formats")
public class FormatController {

    @Autowired
    private FormatRepository formatRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Format create(@RequestBody Format format) {
        return formatRepository.save(format);
    }

    @GetMapping
    public List<Format> all() {
        return formatRepository.findAll();
    }

    @GetMapping("/{id}")
    public Format get(@PathVariable Long id) {
        return formatRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Format update(@PathVariable Long id, @RequestBody Format format) {
        Format exist = formatRepository.findById(id).orElse(null);
        if (exist == null) return null;

        exist.setName(format.getName());
        exist.setDescription(format.getDescription());
        exist.setPrice(format.getPrice());

        return formatRepository.save(exist);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        formatRepository.deleteById(id);
        return "ok";
    }
}
