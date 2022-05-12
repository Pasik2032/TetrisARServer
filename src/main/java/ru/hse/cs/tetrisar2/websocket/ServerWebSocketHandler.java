package ru.hse.cs.tetrisar2.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.SubProtocolCapable;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.hse.cs.tetrisar2.service.session.Game;
import ru.hse.cs.tetrisar2.service.session.GameSession;
import ru.hse.cs.tetrisar2.service.session.SessionService;
import ru.hse.cs.tetrisar2.service.session.UserSession;

import java.io.IOException;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

public class ServerWebSocketHandler extends TextWebSocketHandler implements SubProtocolCapable {

    private static final Logger logger = LoggerFactory.getLogger(ServerWebSocketHandler.class);

    static public final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    @Autowired
    private SessionService sessionService;

    @Autowired
    private GameSession gameSession;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("Server connection opened");
        sessions.add(session);
        TextMessage message = new TextMessage("one-time message from server");
        logger.info("Server sends: {}", message);
        session.sendMessage(message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws IOException {
        logger.info("Server connection closed: {}", status);
        Optional<UserSession> userSessionOptional = sessionService.usersOnline.stream().filter(i -> i.getSession() == session).findFirst();
        Optional<Game> game = gameSession.games.stream().filter(i -> i.getFist() == userSessionOptional.get() || i.getSecond() == userSessionOptional.get()).findFirst();
        if (game.isPresent()) {
            game.get().cancel(userSessionOptional.get());
        }
        sessionService.delUser(session);
    }

    @Scheduled(fixedRate = 10000)
    void sendPeriodicMessages() throws IOException {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                String broadcast = "server periodic message " + LocalTime.now();
                logger.info("Server sends: {}", broadcast);
                session.sendMessage(new TextMessage(broadcast));
            }
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String request = message.getPayload();
        Optional<UserSession> userSessionOptional = sessionService.usersOnline.stream().filter(i -> i.getSession() == session).findFirst();

        if (userSessionOptional.isEmpty()) {
            sessionService.addUser(session, request);
            logger.info("Server received add user: {}", request);
            return;
        }
        UserSession userSession = userSessionOptional.get();
        logger.info("{} message: {}", userSession.getUser().getUsername(), request);
        switch (userSession.status) {
            case ONLINE -> sessionService.onlineRouter(userSession, request);
            case READY, REQUEST, PLAY -> gameSession.gameRouter(userSession, request);
        }

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        logger.info("Server transport error: {}", exception.getMessage());
    }

    @Override
    public List<String> getSubProtocols() {
        return Collections.singletonList("subprotocol.demo.websocket");
    }
}

