package ru.hse.cs.tetrisar2.service.session;

import org.springframework.web.socket.TextMessage;

import java.io.IOException;

public class Game {

    private final UserSession fist;
    private final UserSession second;

    public Game(UserSession fist, UserSession second) {
        this.fist = fist;
        this.second = second;
    }

    public boolean isGame(UserSession user){
        return user == fist || user == second;
    }

    public UserSession getFist() {
        return fist;
    }

    public UserSession getSecond() {
        return second;
    }

    public void cancel(){
        fist.status = Status.ONLINE;
        second.status = Status.ONLINE;
    }
    //    public String getId() {
//        return fist.getId() + "-" + second.getId();
//    }
}
