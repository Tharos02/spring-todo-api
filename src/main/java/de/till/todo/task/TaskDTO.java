package de.till.todo.task;

import de.till.todo.user.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class TaskDTO {

    @NotNull
    @Size(min = 1, max = 255, message = "Der Titel ist zu lang, limit ist 255")
    private String title;

    @Size(max = 1024, message = "Die Beschreibung ist zu lang, limit ist 1024")
    private String description;

    @NotNull
    private Priority priority;

    @NotNull
    private Long userId;

    public @NotNull Long getUserId() {
        return userId;
    }

    public void setUserId(@NotNull Long userId) {
        this.userId = userId;
    }

    public @NotNull @Size(min = 1, max = 255, message = "Der Titel ist zu lang, limit ist 255") String getTitle() {
        return title;
    }

    public void setTitle(@NotNull @Size(min = 1, max = 255, message = "Der Titel ist zu lang, limit ist 255") String title) {
        this.title = title;
    }

    public @Size(max = 1024, message = "Die Beschreibung ist zu lang, limit ist 1024") String getDescription() {
        return description;
    }

    public void setDescription(@Size(max = 1024, message = "Die Beschreibung ist zu lang, limit ist 1024") String description) {
        this.description = description;
    }

    public @NotNull Priority getPriority() {
        return priority;
    }

    public void setPriority(@NotNull Priority priority) {
        this.priority = priority;
    }

    public Task toTask(User user) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setPriority(priority);
        task.setUser(user);
        return task;
    }
}
