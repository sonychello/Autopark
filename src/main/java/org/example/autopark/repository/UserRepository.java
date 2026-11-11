package org.example.autopark.repository;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.autopark.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class UserRepository {
    public User findByNickName(String nickName) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File users = new File("config/users.json");
        JsonNode userNode = mapper.readTree(users);

        if (userNode == null || userNode.isNull()) {
            return null;
        }

        return mapper.treeToValue(userNode.get(nickName), User.class);
    }
}
