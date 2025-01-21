package de.till.todo.user.auth;

import de.till.todo.user.User;
import org.springframework.stereotype.Component;

@Component
public class LoginMapper {

    public LoginResponseDTO toLoginResponseDTO(String token, User user) {
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
        loginResponseDTO.setToken(token);
        loginResponseDTO.setEmail(user.getEmail());
        loginResponseDTO.setUsername(user.getUsername());
        return loginResponseDTO;
    }
}
