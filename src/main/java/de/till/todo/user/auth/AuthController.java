package de.till.todo.user.auth;

import de.till.todo.user.*;
import de.till.todo.user.token.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final LoginMapper loginMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserService userService, JwtUtil jwtUtil, UserMapper userMapper, LoginMapper loginMapper, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
        this.loginMapper = loginMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(path = "/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserDTO userDTO) {
        User user = userDTO.toUser();
        User registeredUser = userService.register(user);
        String token = jwtUtil.generateToken(registeredUser);

        return ResponseEntity.ok(userMapper.toUserResponseDTO(registeredUser));
    }

    @PostMapping(path = "/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginDTO loginDTO, HttpServletResponse response) {
        User existingUser = userService.findByEmail(loginDTO.getEmail());
        if (existingUser != null && passwordEncoder.matches(loginDTO.getPassword(), existingUser.getPassword())) {
            String token = jwtUtil.generateToken(existingUser);

            ResponseCookie cookie = ResponseCookie.from("accessToken")
                    .value(token)
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("None")
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60)
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            return ResponseEntity.status(OK).build();
        }
        return ResponseEntity.status(UNAUTHORIZED).build();
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<String> logoutUser() {
        return ResponseEntity.ok("{message: Logout successful}");
    }

    @GetMapping(path = "/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser() {
        User currentUser = userService.getCurrentUser();

        if (currentUser == null) {
            return ResponseEntity.status(UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(userMapper.toUserResponseDTO(currentUser));
    }
}
