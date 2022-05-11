package ru.hse.cs.tetrisar2.service.session;

import org.springframework.web.socket.WebSocketSession;
import ru.hse.cs.tetrisar2.model.User;

public class UserSession {
    private final User user;
    private final WebSocketSession session;
    public Status status = Status.ONLINE;

    public UserSession(User user, WebSocketSession session) {
        this.user = user;
        this.session = session;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSession that = (UserSession) o;
        return user.getId() == that.user.getId() || session == that.session;
    }

    public User getUser() {
        return user;
    }

    public WebSocketSession getSession() {
        return session;
    }
}
