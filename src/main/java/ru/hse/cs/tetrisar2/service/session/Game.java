package ru.hse.cs.tetrisar2.service.session;

import org.springframework.web.socket.TextMessage;

import java.io.IOException;

public class Game {

    private final UserSession fist;
    private final UserSession second;
    public boolean isReadyFist = false;
    public boolean isReadySecond = false;

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

    public void cancel() throws IOException {
        fist.status = Status.ONLINE;
        second.status = Status.ONLINE;
        fist.getSession().sendMessage(new TextMessage("cancel"));
        second.getSession().sendMessage(new TextMessage("cancel"));
    }

    public void cancel(UserSession user) throws IOException {
        if (fist == user){
            second.status = Status.ONLINE;
            second.getSession().sendMessage(new TextMessage("cancel"));
        } else {
            fist.status = Status.ONLINE;
            fist.getSession().sendMessage(new TextMessage("cancel"));
        }

    }
    //    public String getId() {
//        return fist.getId() + "-" + second.getId();
//    }
}
