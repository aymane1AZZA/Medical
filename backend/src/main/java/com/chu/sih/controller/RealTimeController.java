package com.chu.sih.controller;

import com.chu.sih.security.CurrentActor;
import com.chu.sih.service.ApheresisSessionService;
import com.chu.sih.service.RealTimeEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RestController @RequestMapping("/api/realtime") @RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class RealTimeController {
    private final RealTimeEventService realtime;
    private final ApheresisSessionService sessions;
    private final CurrentActor actor;
    @GetMapping(value="/me",produces=MediaType.TEXT_EVENT_STREAM_VALUE) public SseEmitter me(){return realtime.subscribeUser(actor.id());}
    @GetMapping(value="/sessions/{id}",produces=MediaType.TEXT_EVENT_STREAM_VALUE) public SseEmitter session(@PathVariable UUID id){sessions.get(id);return realtime.subscribeSession(id);}
}
