package com.sagarmalasi.project.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "tasks",
        uniqueConstraints =
        @UniqueConstraint(name = "UniqueProjectAndTask",columnNames = {"title","project_id"}))
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false,columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Column(nullable = false)
    private Double estimatedHours;

    private Double actualHours;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToMany(mappedBy = "task",cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    private List<TaskAssignment> taskAssignments = new ArrayList<>();

    @OneToMany(mappedBy = "task",cascade = CascadeType.PERSIST)
    private List<TaskHistory> taskHistories = new ArrayList<>();

    @OneToMany(mappedBy = "task",cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    private List<RiskAssessment> riskAssessments = new ArrayList<>();

    @OneToMany(mappedBy = "task",cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    private List<WorkloadSnapshot> workloadSnapshots = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }


    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
    }


    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }




}
