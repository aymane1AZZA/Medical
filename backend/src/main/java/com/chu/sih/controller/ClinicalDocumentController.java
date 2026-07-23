package com.chu.sih.controller;

import com.chu.sih.dto.ClinicalRequests.DocumentMetadata;
import com.chu.sih.entity.ClinicalDocument;
import com.chu.sih.service.ClinicalDocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clinical/documents")
@RequiredArgsConstructor
public class ClinicalDocumentController {
    private final ClinicalDocumentService documents;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<ClinicalDocument> list(@RequestParam UUID patientId) { return documents.list(patientId); }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','MEDECIN_SPECIALISTE','MEDECIN_BIOLOGISTE','INFERMIER','LABO')")
    public ClinicalDocument upload(@Valid @RequestPart("metadata") DocumentMetadata metadata, @RequestPart("file") MultipartFile file) {
        return documents.upload(metadata, file);
    }

    @PostMapping(value = "/{id}/versions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','MEDECIN_SPECIALISTE','MEDECIN_BIOLOGISTE','INFERMIER','LABO')")
    public ClinicalDocument version(@PathVariable UUID id, @RequestPart("file") MultipartFile file) {
        return documents.uploadVersion(id, file);
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource> download(@PathVariable UUID id) {
        var document = documents.get(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.getMimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getTitle().replace("\"", "'") + "\"")
                .body(documents.resource(id));
    }

    @PostMapping("/{id}/sign")
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','MEDECIN_SPECIALISTE','MEDECIN_BIOLOGISTE')")
    public ClinicalDocument sign(@PathVariable UUID id) { return documents.sign(id); }

    @PostMapping("/{id}/withdraw")
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','MEDECIN_SPECIALISTE','MEDECIN_BIOLOGISTE')")
    public ClinicalDocument withdraw(@PathVariable UUID id, @RequestParam String reason) { return documents.withdraw(id, reason); }
}
