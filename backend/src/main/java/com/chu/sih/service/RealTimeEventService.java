package com.chu.sih.service;

import com.chu.sih.entity.EventOutbox;
import com.chu.sih.repository.EventOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service @RequiredArgsConstructor
public class RealTimeEventService {
    private final EventOutboxRepository outbox;
    private final Map<Long, CopyOnWriteArrayList<SseEmitter>> userStreams = new ConcurrentHashMap<>();
    private final Map<UUID, CopyOnWriteArrayList<SseEmitter>> sessionStreams = new ConcurrentHashMap<>();

    public SseEmitter subscribeUser(long userId){return subscribe(userStreams.computeIfAbsent(userId, ignored -> new CopyOnWriteArrayList<>()));}
    public SseEmitter subscribeSession(UUID sessionId){return subscribe(sessionStreams.computeIfAbsent(sessionId, ignored -> new CopyOnWriteArrayList<>()));}

    public void user(long userId, String type, UUID aggregateId, String payload){
        persist("USER", aggregateId, type, userId, payload);
        publish(userStreams.get(userId), type, payload);
    }

    public void session(UUID sessionId, String type, String payload){
        persist("APHERESIS_SESSION", sessionId, type, null, payload);
        publish(sessionStreams.get(sessionId), type, payload);
    }

    private SseEmitter subscribe(CopyOnWriteArrayList<SseEmitter> streams){
        var emitter = new SseEmitter(30L * 60L * 1000L);
        streams.add(emitter);
        emitter.onCompletion(() -> streams.remove(emitter));
        emitter.onTimeout(() -> streams.remove(emitter));
        emitter.onError(ignored -> streams.remove(emitter));
        try { emitter.send(SseEmitter.event().name("connected").data(Map.of("at", Instant.now().toString()))); }
        catch (IOException error) { streams.remove(emitter); emitter.completeWithError(error); }
        return emitter;
    }

    private void publish(CopyOnWriteArrayList<SseEmitter> streams, String type, String payload){
        if(streams == null) return;
        streams.forEach(emitter -> {
            try { emitter.send(SseEmitter.event().name(type).data(payload)); }
            catch (IOException error) { streams.remove(emitter); emitter.complete(); }
        });
    }

    private void persist(String aggregateType, UUID aggregateId, String type, Long recipientId, String payload){
        outbox.save(EventOutbox.builder().aggregateType(aggregateType).aggregateId(aggregateId).eventType(type)
                .recipientId(recipientId).payload(payload == null ? "{}" : payload).build());
    }
}
