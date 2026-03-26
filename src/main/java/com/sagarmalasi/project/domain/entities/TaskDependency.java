package com.sagarmalasi.project.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "task_dependencies",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"predecessor_task_id", "successor_task_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskDependency {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Task A (must finish first)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "predecessor_task_id", nullable = false)
    private Task predecessorTask;

    // Task B (depends on A)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "successor_task_id", nullable = false)
    private Task successorTask;
}