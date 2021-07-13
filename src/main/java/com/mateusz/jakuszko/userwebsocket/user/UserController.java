package com.mateusz.jakuszko.userwebsocket.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("users")
@Controller
public class UserController {

    private final UserRepository repository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/user-endpoint")
    @SendTo("/topic/user")
    public UserDto save(UserDto userRequest) {
        User user = repository.save(User.fromUserDto(userRequest));
        return User.fromUser(user);
    }

    @GetMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> get() {
        return repository.findAll().stream()
                .map(User::fromUser)
                .collect(Collectors.toList());
    }

    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody UserDto userDto) {
        UserDto userFromDb = User.fromUser(repository.save(User.fromUserDto(userDto)));
        simpMessagingTemplate.convertAndSend("/topic/user", userFromDb);
        return userFromDb;
    }

    @GetMapping("/topic")
    @ResponseBody
    public void pushUsersToTopic() {
        repository.findAll().stream()
                .map(User::fromUser)
                .forEach(user -> simpMessagingTemplate.convertAndSend("/topic/user", user));
    }
}
