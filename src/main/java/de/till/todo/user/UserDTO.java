package de.till.todo.user;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserDTO {

    @NotNull
    @Size(min = 1, max = 32, message = "Der Username ist zu lang, limit ist 32")
    private String username;

    @NotNull
    @Size(min = 8, message = "Das Passwort muss mindestens 8 Zeichen beinhalten")
    private String password;

    @NotNull
    @Email
    private String email;

    public @NotNull @Size(min = 1, max = 32, message = "Der Username ist zu lang, limit ist 32") String getUsername() {
        return username;
    }

    public void setUsername(@NotNull @Size(min = 1, max = 32, message = "Der Username ist zu lang, limit ist 32") String username) {
        this.username = username;
    }

    public @NotNull @Size(min = 8, message = "Das Passwort muss mindestens 8 Zeichen beinhalten") String getPassword() {
        return password;
    }

    public void setPassword(@NotNull @Size(min = 8, message = "Das Passwort muss mindestens 8 Zeichen beinhalten") String password) {
        this.password = password;
    }

    public @NotNull String getEmail() {
        return email;
    }

    public void setEmail(@NotNull String email) {
        this.email = email;
    }

    public User toUser() {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        return user;
    }
}
