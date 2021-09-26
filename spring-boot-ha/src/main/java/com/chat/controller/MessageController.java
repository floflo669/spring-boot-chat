package com.chat.controller;

import com.chat.request.message.CreateMessageRequest;
import com.chat.request.message.PatchMessageRequest;
import com.chat.request.message.UpdateMessageRequest;
import com.chat.response.message.MessageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;

import javax.validation.Valid;
import java.util.UUID;

@RequestMapping(value = "/message")
public interface MessageController {

    @GetMapping("/{id}")
    ResponseEntity<MessageResponse> getMessage(@PathVariable("id") UUID id);

    @PostMapping
    ResponseEntity<Void> postMessage(@RequestBody @Valid CreateMessageRequest createMessageRequest);

    @PutMapping("/{id}")
    ResponseEntity<Void> putMessage(@PathVariable("id") UUID id, @RequestBody @Valid UpdateMessageRequest updateMessageRequest);

    @PatchMapping("/{id}")
    ResponseEntity<Void> patchMessage(@PathVariable("id") UUID id, @RequestBody @Valid PatchMessageRequest patchMessageRequest);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteMessage(@PathVariable("id") UUID id);
}
