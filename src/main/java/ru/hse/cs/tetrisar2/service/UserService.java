package ru.hse.cs.tetrisar2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hse.cs.tetrisar2.Model.User;
import ru.hse.cs.tetrisar2.entity.UserEntity;
import ru.hse.cs.tetrisar2.exception.UserAlredyExistException;
import ru.hse.cs.tetrisar2.exception.UserNotFoundException;
import ru.hse.cs.tetrisar2.respository.UserRepo;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    public UserEntity registration(UserEntity userEntity) throws UserAlredyExistException {
        if (userRepo.findByUsername(userEntity.getUsername()) != null){
            throw new UserAlredyExistException("Пользователь с таким именнем уже существует");
        }
        return userRepo.save(userEntity);
    }

    public User getUser(Long id) throws UserNotFoundException {
        UserEntity userEntity = userRepo.findById(id).get();
        if (userEntity == null){
            throw new UserNotFoundException("пользователь не найден");
        }
        return User.toModel(userEntity);
    }
}
