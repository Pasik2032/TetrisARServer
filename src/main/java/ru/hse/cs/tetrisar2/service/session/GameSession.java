package ru.hse.cs.tetrisar2.service.session;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
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
        games.remove(game);
    }

    private String generationShape(){
        StringBuilder str = new StringBuilder();
        str.append("generation: ");
        Random random = new Random();
        for (int i = 0; i < 30; i++) {
            str.append(random.nextInt(6)).append(" ");
        }
        return str.toString();
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
                    String string = generationShape();
                    game.getFist().getSession().sendMessage(new TextMessage(string));
                    game.getSecond().getSession().sendMessage(new TextMessage(string));
                } else if (Objects.equals(str, "cancel")) {
                    cancelGame(game);
                }
            }
            case READY -> cancelGame(game);
            case PLAY -> {
                if (Objects.equals(str, "exit")){
                    cancelGame(game);
                } else if  (Objects.equals(str, "finish")){
                    game.isReadyFist = false;
                    game.isReadySecond = false;
                } else if (Objects.equals(str, "generation")){
                    String string = generationShape();
                    game.getFist().getSession().sendMessage(new TextMessage(string));
                    game.getSecond().getSession().sendMessage(new TextMessage(string));
                } else if (Objects.equals(str, "ready")){
                    if (game.getFist() == user) {
                        game.isReadyFist = true;
                    } else {
                        game.isReadySecond = true;
                    }
                    if (game.isReadyFist && game.isReadySecond){
                        game.getFist().getSession().sendMessage(new TextMessage("game"));
                        game.getSecond().getSession().sendMessage(new TextMessage("game"));
                    }
                } else {
                    if (game.getFist() == user) {
                        game.getSecond().getSession().sendMessage(new TextMessage(str));
                    } else {
                        game.getFist().getSession().sendMessage(new TextMessage(str));
                    }
                }
            }
        }
    }
}
