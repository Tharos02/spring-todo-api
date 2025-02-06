package de.till.todo.task;

import de.till.todo.user.User;
import de.till.todo.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final TaskMapper taskMapper;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserService userService, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.taskMapper = taskMapper;
    }

    public List<Task> getTasks() {
        return taskRepository.findAll();
    }

    @Transactional
    public TaskResponseDTO createTask(TaskDTO taskDTO) {
        User currentUser = userService.getCurrentUser();

        if (currentUser == null) {
            throw new NoSuchElementException("User not found"); // Hier kannst du eine benutzerdefinierte Ausnahme werfen
        }

        Task newTask = taskDTO.toTask();
        newTask.setUser(currentUser);

        Task createdTask = taskRepository.save(newTask);

        return taskMapper.toTaskResponseDTO(createdTask);
    }

    public void deleteTask(Long id) {
        User currentUser = userService.getCurrentUser();

        if (currentUser == null) {
            throw new NoSuchElementException("User not found");
        }

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Task not found"));

        if (task.getUser() == null || !task.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(UNAUTHORIZED, "User is not authorized to delete this task");
        }
        taskRepository.delete(task);
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task with ID " + id + " not found."));
    }

    public List<TaskResponseDTO> getTaskForCurrentUser() {
        User currentUser = userService.getCurrentUser();

        if (currentUser == null) {
            throw new NoSuchElementException("User not found");
        }

        List<Task> tasks = taskRepository.findByUser(currentUser);

        return tasks.stream()
                .map(taskMapper::toTaskResponseDTO)
                .toList();
    }

    public TaskResponseDTO updateTask(Long id, TaskDTO taskDTO) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new NoSuchElementException("User not found");
        }

        Task currentTask = taskRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Task not found"));
        currentTask.setStatus(taskDTO.getStatus());
        currentTask.setPriority(taskDTO.getPriority());
        currentTask.setDescription(taskDTO.getDescription());

        Task updatedTask = taskRepository.save(currentTask);

        return taskMapper.toTaskResponseDTO(updatedTask);
    }

}
