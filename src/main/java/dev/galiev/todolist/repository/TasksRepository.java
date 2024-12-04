package dev.galiev.todolist.repository;

import dev.galiev.todolist.model.Task;
import dev.galiev.todolist.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TasksRepository extends JpaRepository<Task, Long> {
    Page<Task> findByAuthorId(Long authorId, Pageable pageable);
    Page<Task> findByAssignedUserId(Long assignedUserId, Pageable pageable);
    List<Task> findByAuthor(User user);
}
