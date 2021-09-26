package com.chat.repository;

import com.chat.model.MessageModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<MessageModel, UUID>, CrudRepository<MessageModel, UUID> {
}
