package com.sagarmalasi.project.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.query.SelectionQuery;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(
        name = "tasks",
        uniqueConstraints = @UniqueConstraint(
                name = "UniqueProjectAndTask",
                columnNames = {"title", "project_id"}
        )
)
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Version
    private Long version;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    // PLANNED SCHEDULE
    @Column(nullable = false)
    private LocalDate plannedStartDate;

    @Column(nullable = false)
    private LocalDate plannedEndDate;

    // ACTUAL TRACKING
    private LocalDate actualStartDate;
    private LocalDate actualEndDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // SUCCESSOR DEPENDENCIES (Tasks depending on THIS task)
    @OneToMany(mappedBy = "predecessorTask", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskDependency> successorDependencies = new ArrayList<>();

    // PREDECESSOR DEPENDENCIES (Tasks this task depends on)
    @OneToMany(mappedBy = "successorTask", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskDependency> predecessorDependencies = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @OneToMany(mappedBy = "task", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<TaskAssignment> taskAssignments = new ArrayList<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.PERSIST)
    private List<TaskHistory> taskHistories = new ArrayList<>();

    public long getPlannedDurationDays() {
        return ChronoUnit.DAYS.between(plannedStartDate, plannedEndDate);
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = TaskStatus.TODO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return id != null && id.equals(task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }


}