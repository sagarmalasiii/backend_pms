package com.sagarmalasi.project.services;

import com.sagarmalasi.project.domain.dtos.TaskDto;
import com.sagarmalasi.project.domain.entities.Task;
import com.sagarmalasi.project.mappers.TaskMapper;
import com.sagarmalasi.project.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public  List<TaskDto> getAllProjectAssociatedTasks(UUID projectId) {
        List<Task> tasks = taskRepository.findAllTasks(projectId);
        return tasks.stream().map(taskMapper::toDto).toList();

    }
}
