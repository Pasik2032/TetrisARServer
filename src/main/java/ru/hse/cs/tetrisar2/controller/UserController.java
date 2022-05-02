package ru.hse.cs.tetrisar2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import ru.hse.cs.tetrisar2.exception.UserAlredyExistException;
import ru.hse.cs.tetrisar2.exception.UserNotFoundException;
import ru.hse.cs.tetrisar2.entity.UserEntity;
import ru.hse.cs.tetrisar2.service.UserService;
import ru.hse.cs.tetrisar2.websocket.ServerWebSocketHandler;

import java.time.LocalTime;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public ResponseEntity registration(@RequestBody UserEntity userEntity){
        try {
            userService.registration(userEntity);
            return ResponseEntity.ok("Пользователь успешно создан");
        } catch (UserAlredyExistException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error");
        }
    }

    @GetMapping
    public ResponseEntity getOneUser(@RequestParam Long id){
        try {
            return ResponseEntity.ok(userService.getUser(id));
        } catch (UserNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error");
        }
    }

    @GetMapping("/ws")
    public ResponseEntity getws(@RequestParam Long id){
        try {
            for (WebSocketSession session : ServerWebSocketHandler.sessions) {
                if (session.isOpen()) {
                    String broadcast = "server get " + id;
                    session.sendMessage(new TextMessage(broadcast));
                }
            }
            return ResponseEntity.ok(userService.getUser(id));
        } catch (UserNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error");
        }
    }
}
