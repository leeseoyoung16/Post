package com.example.memo.report;

import com.example.memo.post.Post;
import com.example.memo.post.PostResponse;
import com.example.memo.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long>
{
    Optional<Report> findByUserAndPost(User user, Post post);

    @Query("SELECT r.post From Report r GROUP BY r.post HAVING COUNT(r.id) >= 5")
    List<Post> findPostsWithMoreThanFiveReports();
}
