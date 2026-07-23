package com.chu.sih.service;

import com.chu.sih.dto.ClinicalRequests.DocumentMetadata;
import com.chu.sih.entity.ClinicalDocument;
import com.chu.sih.exception.BadRequestException;
import com.chu.sih.exception.ResourceNotFoundException;
import com.chu.sih.repository.ClinicalDocumentRepository;
import com.chu.sih.security.CurrentActor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClinicalDocumentService {
    private static final long MAX_BYTES = 25L * 1024L * 1024L;

    private final ClinicalDocumentRepository documents;
    private final ClinicalAccessService access;
    private final CurrentActor actor;
    private final AuditService audit;

    @Value("${app.documents.storage-path:./storage/documents}")
    private String storagePath;

    @Transactional(readOnly = true)
    public List<ClinicalDocument> list(UUID patientId) {
        access.requirePatient(patientId);
        return documents.findByPatientIdAndDeletedAtIsNullOrderByCreatedAtDesc(patientId);
    }

    @Transactional
    public ClinicalDocument upload(DocumentMetadata metadata, MultipartFile file) {
        access.requirePatient(metadata.patientId());
        validate(file);
        StoredFile stored = store(file);
        var document = documents.save(ClinicalDocument.builder()
                .patientId(metadata.patientId())
                .documentType(metadata.documentType())
                .title(metadata.title())
                .description(metadata.description())
                .confidentiality(metadata.confidentiality() == null ? "CLINICAL" : metadata.confidentiality())
                .storageKey(stored.key())
                .mimeType(file.getContentType() == null ? "application/octet-stream" : file.getContentType())
                .sizeBytes(file.getSize())
                .checksumSha256(stored.checksum())
                .authoredBy(actor.id())
                .status("CURRENT")
                .versionNumber(1)
                .build());
        audit.record("CLINICAL_DOCUMENT_UPLOADED", "CREATE", "ClinicalDocument", document.getId(), metadata.patientId(), "{}");
        return document;
    }

    @Transactional
    public ClinicalDocument uploadVersion(UUID documentId, MultipartFile file) {
        var previous = get(documentId);
        validate(file);
        StoredFile stored = store(file);
        previous.setStatus("SUPERSEDED");
        var document = documents.save(ClinicalDocument.builder()
                .patientId(previous.getPatientId())
                .documentType(previous.getDocumentType())
                .title(previous.getTitle())
                .description(previous.getDescription())
                .confidentiality(previous.getConfidentiality())
                .storageKey(stored.key())
                .mimeType(file.getContentType() == null ? "application/octet-stream" : file.getContentType())
                .sizeBytes(file.getSize())
                .checksumSha256(stored.checksum())
                .authoredBy(actor.id())
                .status("CURRENT")
                .versionNumber(previous.getVersionNumber() + 1)
                .supersedesId(previous.getId())
                .build());
        audit.record("CLINICAL_DOCUMENT_VERSIONED", "CREATE", "ClinicalDocument", document.getId(), previous.getPatientId(), "{}");
        return document;
    }

    @Transactional(readOnly = true)
    public ClinicalDocument get(UUID id) {
        var document = documents.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new ResourceNotFoundException("Document clinique introuvable."));
        access.requirePatient(document.getPatientId());
        return document;
    }

    @Transactional(readOnly = true)
    public Resource resource(UUID id) {
        var document = get(id);
        try {
            return new UrlResource(root().resolve(document.getStorageKey()).normalize().toUri());
        } catch (Exception exception) {
            throw new ResourceNotFoundException("Fichier document introuvable.");
        }
    }

    @Transactional
    public ClinicalDocument sign(UUID id) {
        var document = get(id);
        if (document.getStatus().equals("WITHDRAWN")) throw new BadRequestException("Un document retire ne peut pas etre signe.");
        document.setStatus("SIGNED");
        document.setSignedBy(actor.id());
        document.setSignedAt(Instant.now());
        audit.record("CLINICAL_DOCUMENT_SIGNED", "UPDATE", "ClinicalDocument", document.getId(), document.getPatientId(), "{}");
        return document;
    }

    @Transactional
    public ClinicalDocument withdraw(UUID id, String reason) {
        if (reason == null || reason.isBlank()) throw new BadRequestException("Un motif est obligatoire.");
        var document = get(id);
        document.setStatus("WITHDRAWN");
        document.setDeletedAt(Instant.now());
        document.setDeletedBy(actor.id());
        audit.record("CLINICAL_DOCUMENT_WITHDRAWN", "UPDATE", "ClinicalDocument", document.getId(), document.getPatientId(),
                "{\"reason\":\"" + reason.replace("\"", "'") + "\"}");
        return document;
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new BadRequestException("Le fichier est obligatoire.");
        if (file.getSize() > MAX_BYTES) throw new BadRequestException("Le document depasse 25 Mo.");
    }

    private StoredFile store(MultipartFile file) {
        try {
            Files.createDirectories(root());
            String extension = extension(file.getOriginalFilename());
            String key = UUID.randomUUID() + extension;
            Path target = root().resolve(key).normalize();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (InputStream input = new DigestInputStream(file.getInputStream(), digest)) {
                Files.copy(input, target);
            }
            return new StoredFile(key, HexFormat.of().formatHex(digest.digest()));
        } catch (Exception exception) {
            throw new BadRequestException("Impossible de stocker le document clinique.");
        }
    }

    private Path root() {
        return Path.of(storagePath).toAbsolutePath().normalize();
    }

    private String extension(String name) {
        if (name == null) return "";
        int index = name.lastIndexOf('.');
        if (index < 0 || index == name.length() - 1) return "";
        String ext = name.substring(index).toLowerCase();
        return ext.length() > 12 ? "" : ext;
    }

    private record StoredFile(String key, String checksum) {}
}
