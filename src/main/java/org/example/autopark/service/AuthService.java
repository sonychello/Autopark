package org.example.autopark.service;

import org.example.autopark.model.User;
import org.example.autopark.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.util.Optional;

public class AuthService {

    private static final UserRepository userRepository = new UserRepository();

    public Optional<User> checkUser(String nickName, String password) throws IOException {

        User user = userRepository.findByNickName(nickName);

        if (user == null) {
            return Optional.empty();
        }

        if (!BCrypt.checkpw(password, user.passwordHash())) {
            return Optional.empty();
        }

        return Optional.of(user);

    }

}
