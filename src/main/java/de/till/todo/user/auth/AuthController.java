package de.till.todo.user.auth;

import de.till.todo.user.*;
import de.till.todo.user.token.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginMapper loginMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping(path = "/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserDTO userDTO) {
        User user = userDTO.toUser();
        User registeredUser = userService.register(user);
        String token = jwtUtil.generateToken(registeredUser);

        return ResponseEntity.ok(userMapper.toUserResponseDTO(registeredUser));
    }

    @PostMapping(path = "/login")
    public ResponseEntity<LoginResponseDTO> loginUser(@Valid @RequestBody LoginDTO loginDTO) {
        User existingUser = userService.findByEmail(loginDTO.getEmail());
        if(existingUser != null && passwordEncoder.matches(loginDTO.getPassword(), existingUser.getPassword())) {
            String token = jwtUtil.generateToken(existingUser);

            return ResponseEntity.ok(loginMapper.toLoginResponseDTO(token, existingUser));
        }
        return ResponseEntity.status(UNAUTHORIZED).build();
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<String> logoutUser() {
        return ResponseEntity.ok("{message: Logout successful}");
    }
}
