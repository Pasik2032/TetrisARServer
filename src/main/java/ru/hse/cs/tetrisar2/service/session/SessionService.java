package ru.hse.cs.tetrisar2.service.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import ru.hse.cs.tetrisar2.model.User;
import ru.hse.cs.tetrisar2.entity.UserEntity;
import ru.hse.cs.tetrisar2.respository.UserRepo;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class SessionService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private GameSession gameSession;

    public  Set<UserSession> usersOnline =  new CopyOnWriteArraySet<>();

     public void addUser(WebSocketSession session, String str) throws IOException {
         String[] input = str.split(" ");
         String login = input[0];
         String password = input[1];
         UserEntity userEntity = userRepo.findByUsername(login);
         if (userEntity == null){
             session.sendMessage(new TextMessage("Такой пользователь не существует"));
             return;
         }
         if (!Objects.equals(userEntity.getPassword(), password)) {
             session.sendMessage(new TextMessage("Пароль не верный"));
             return;
         }

        UserSession userSession = new UserSession(User.toModel(userEntity), session);
        if (usersOnline.add(userSession)){
            session.sendMessage(new TextMessage("Соеденение установленно"));
        } else {
            session.sendMessage(new TextMessage("У пользователя есть активная сессия"));
        }
    }

     public void delUser(WebSocketSession session){
        usersOnline.removeIf(i -> i.getSession() == session);
    }

    public void onlineRouter(UserSession user, String str) throws IOException {
         String[] input = str.split(" ");
         if (Objects.equals(input[0], "response")){
             if (input.length != 2){
                 user.getSession().sendMessage(new TextMessage("Username не коректный"));
                 return;
             }
             Optional<UserSession> second = usersOnline.stream().filter(
                     i -> Objects.equals(i.getUser().getUsername(), input[1])).findFirst();
             if (second.isEmpty()){
                 user.getSession().sendMessage(new TextMessage("Username не коректный"));
                 return;
             }
             if (second.get() == user){
                 user.getSession().sendMessage(new TextMessage("Username не коректный"));
                 return;
             }
             gameSession.createGame(user, second.get());
         } else  if (Objects.equals(str, "exit")){
             usersOnline.remove(user);
         }
    }

}
