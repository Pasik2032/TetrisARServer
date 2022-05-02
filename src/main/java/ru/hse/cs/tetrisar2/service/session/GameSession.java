package ru.hse.cs.tetrisar2.service.session;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class GameSession {

    public  Set<Game> games =  new CopyOnWriteArraySet<>();

    public void createGame(UserSession fist, UserSession second) throws IOException {
        Game game = new Game(fist, second);
        fist.status = Status.READY;
        second.status = Status.REQUEST;
        second.getSession().sendMessage(new TextMessage("request " + fist.getUser().getUsername()));
        games.add(game);
    }

    private void cancelGame(Game game) throws IOException {
        game.cancel();
        game.getFist().getSession().sendMessage(new TextMessage("cancel"));
        game.getSecond().getSession().sendMessage(new TextMessage("cancel"));
        games.remove(game);
    }


    public void gameRouter(UserSession user, String str) throws IOException {
        Game game = games.stream().filter(i -> i.isGame(user)).findFirst().get();
        switch (user.status) {
            case REQUEST -> {
                if (Objects.equals(str, "ok")) {
                    game.getFist().status = Status.PLAY;
                    game.getSecond().status = Status.PLAY;
                    game.getFist().getSession().sendMessage(new TextMessage("start"));
                    game.getSecond().getSession().sendMessage(new TextMessage("start"));
                } else if (Objects.equals(str, "cancel")) {
                    cancelGame(game);
                }
            }
            case READY -> cancelGame(game);
            case PLAY -> {
                if (Objects.equals(str, "exit")){
                    cancelGame(game);
                    return;
                }
                if (game.getFist() == user) {
                    game.getSecond().getSession().sendMessage(new TextMessage(str));
                } else {
                    game.getFist().getSession().sendMessage(new TextMessage(str));
                }
            }
        }
    }
}
