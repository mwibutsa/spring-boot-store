package com.mwibutsa.store.controllers;

import com.mwibutsa.store.entities.Message;
import com.mwibutsa.store.repositories.UserRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

    private final UserRepository userRepository;

    public MessageController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping("/hello")
    public Message sayHello() {
        return new Message("Hello World!");
    }


}
