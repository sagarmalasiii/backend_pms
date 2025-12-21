package com.sagarmalasi.project;

import com.sagarmalasi.project.domain.entities.RiskAssessment;
import com.sagarmalasi.project.domain.entities.Task;
import com.sagarmalasi.project.domain.entities.User;
import com.sagarmalasi.project.domain.entities.UserPerformance;
import com.sagarmalasi.project.repositories.TaskRepository;
import com.sagarmalasi.project.services.RiskAssessmentService;
import com.sagarmalasi.project.services.TaskService;
import com.sagarmalasi.project.services.UserPerformanceService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Transactional
class TaskServiceTest {

    @Autowired
    TaskService taskService;
    @Autowired
    UserPerformanceService performanceService;
    @Autowired
    RiskAssessmentService riskService;
    @Autowired
    TaskRepository taskRepository;

    @Test
    void testTaskCompletionUpdatesPerformanceAndRisk() {
        Task task = taskRepository.save();
        User member = task.getTaskAssignments().get(0).getMember();

        taskService.markTaskCompleted(task.getId());

        UserPerformance performance = performanceService.getByUser(member);
        assertEquals(1, performance.getTotalTaskCompleted());

        List<RiskAssessment> risks = riskService.getAllForTask(task);
        assertFalse(risks.isEmpty());
    }
}

