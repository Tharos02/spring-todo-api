package de.till.todo.user.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public class LoginDTO {

    @NotNull
    @Email(message = "Diese Email ist ungültig")
    private String email;

    @NotNull(message = "Gebe ein Password ein")
    private String password;

    public @NotNull @Email(message = "Diese Email ist ungültig") String getEmail() {
        return email;
    }

    public void setEmail(@NotNull @Email(message = "Diese Email ist ungültig") String email) {
        this.email = email;
    }

    public @NotNull(message = "Gebe ein Password ein") String getPassword() {
        return password;
    }

    public void setPassword(@NotNull(message = "Gebe ein Password ein") String password) {
        this.password = password;
    }
}
