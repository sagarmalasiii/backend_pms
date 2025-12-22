package com.sagarmalasi.project.services;

import com.sagarmalasi.project.domain.entities.Task;
import com.sagarmalasi.project.domain.entities.TaskAssignment;
import com.sagarmalasi.project.domain.entities.User;
import com.sagarmalasi.project.domain.entities.WorkloadSnapshot;
import com.sagarmalasi.project.repositories.WorkloadSnapshotRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkloadSnapShotService {
    private final WorkloadSnapshotRepository workloadSnapshotRepository;
    @Transactional
    public void captureSnapshot(User user, Task task) {

        long activeTaskCount =
                workloadSnapshotRepository.countActiveTasksForUser(user.getId());

        WorkloadSnapshot snapshot = WorkloadSnapshot.builder()
                .member(user)
                .task(task)
                .activeTaskCount((int) activeTaskCount)
                .build();

        workloadSnapshotRepository.save(snapshot);
    }




}
