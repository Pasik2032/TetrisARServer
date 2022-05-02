package ru.hse.cs.tetrisar2.respository;

import org.springframework.data.repository.CrudRepository;
import ru.hse.cs.tetrisar2.entity.UserEntity;

public interface UserRepo extends CrudRepository<UserEntity,Long> {
    UserEntity findByUsername(String username);
}
