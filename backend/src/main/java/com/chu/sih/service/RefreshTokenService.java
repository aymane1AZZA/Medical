package com.chu.sih.service;

import com.chu.sih.entity.RefreshToken;
import com.chu.sih.entity.User;
import com.chu.sih.exception.BadRequestException;
import com.chu.sih.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository repository;
    private final SecureRandom secureRandom = new SecureRandom();
    @Value("${app.jwt.refresh-expiration-ms}") private long expirationMs;

    public record Issued(String raw, RefreshToken entity) {}

    @Transactional
    public Issued issue(User user, UUID familyId, String ip, String userAgent){
        byte[] bytes=new byte[48];secureRandom.nextBytes(bytes);
        String raw=Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        Instant now=Instant.now();
        var token=repository.save(RefreshToken.builder().id(UUID.randomUUID()).userId(user.getId()).tokenHash(hash(raw))
                .familyId(familyId==null?UUID.randomUUID():familyId).issuedAt(now).expiresAt(now.plusMillis(expirationMs))
                .ipAddress(ip).userAgent(limit(userAgent,500)).build());
        return new Issued(raw,token);
    }

    @Transactional
    public RefreshToken consume(String raw){
        var token=repository.findByTokenHash(hash(raw)).orElseThrow(()->new BadRequestException("Session de renouvellement invalide."));
        if(token.getUsedAt()!=null || token.getRevokedAt()!=null){
            Instant now=Instant.now();repository.findByFamilyId(token.getFamilyId()).forEach(t->t.setRevokedAt(now));
            throw new BadRequestException("Réutilisation de session détectée. Toute la famille a été révoquée.");
        }
        if(token.getExpiresAt().isBefore(Instant.now())) throw new BadRequestException("Session expirée.");
        token.setUsedAt(Instant.now());return token;
    }

    @Transactional public void linkReplacement(RefreshToken used,RefreshToken replacement){used.setReplacedById(replacement.getId());}
    @Transactional public void revoke(String raw){repository.findByTokenHash(hash(raw)).ifPresent(t->t.setRevokedAt(Instant.now()));}
    public long expirationMs(){return expirationMs;}
    private String hash(String value){try{return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));}catch(Exception e){throw new IllegalStateException(e);}}
    private String limit(String value,int max){return value==null?null:value.substring(0,Math.min(value.length(),max));}
}
