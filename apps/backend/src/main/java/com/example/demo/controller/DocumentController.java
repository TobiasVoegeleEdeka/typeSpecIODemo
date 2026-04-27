package com.example.demo.controller;

import com.example.demo.api.model.Document;
import com.example.demo.repository.DocumentRepository;
import com.example.demo.exception.SystemFault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/documents")
@CrossOrigin(origins = "http://localhost:4200")
public class DocumentController {

    private final DocumentRepository documentRepository;

    public DocumentController(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @GetMapping
    public List<Document> list() {
        return documentRepository.findAll();
    }

    @PostMapping
    public Document create(@RequestBody Document doc) {
        Document newDoc = new Document(
                doc.id(),
                doc.name(),
                doc.content(),
                doc.documentType(),
                LocalDateTime.now());
        return documentRepository.save(newDoc);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Document> read(@PathVariable int id) {
        return documentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new SystemFault("Document not found", "NOT_FOUND", 404));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable int id) {
        if (!documentRepository.findById(id).isPresent()) {
            throw new SystemFault("Document not found", "NOT_FOUND", 404);
        }
        documentRepository.delete(id);
        return ResponseEntity.noContent().build();
    }
}
