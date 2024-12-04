package dev.galiev.todolist.repository;

import dev.galiev.todolist.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentsRepository extends JpaRepository<Comment, Long> {
}
