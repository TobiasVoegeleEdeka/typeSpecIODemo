package com.example.demo.controller;

import com.example.demo.api.api.DocumentsApi;
import com.example.demo.api.model.Document;
import com.example.demo.repository.DocumentRepository;

import com.example.demo.exception.SystemFault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@Validated
public class DocumentController implements DocumentsApi {

    private final DocumentRepository documentRepository;

    public DocumentController(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Override
    public List<Document> documentsList() {
        return documentRepository.findAll();
    }

    @Override
    public Document documentsCreate(Document doc) {
        Document newDoc = new Document(
                doc.id(),
                doc.name(),
                doc.content(),
                doc.documentType(),
                doc.documentUuid(),
                LocalDateTime.now());
        return documentRepository.save(newDoc);
    }

    @Override
    public Document documentsRead(Integer id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new SystemFault("Document not found", "NOT_FOUND", 404));
    }

    @Override
    public void documentsDelete(Integer id) {
        if (!documentRepository.findById(id).isPresent()) {
            throw new SystemFault("Document not found", "NOT_FOUND", 404);
        }
        documentRepository.delete(id);
    }
}
