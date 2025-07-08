package com.example.memo.service;

import com.example.memo.post.Post;
import com.example.memo.post.PostRepository;
import com.example.memo.report.Report;
import com.example.memo.report.ReportRepository;
import com.example.memo.report.ReportService;
import com.example.memo.user.User;
import com.example.memo.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ReportServiceTest {

    @Autowired private ReportService reportService;
    @Autowired private ReportRepository reportRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private UserRepository userRepository;

    @Test
    @DisplayName("신고 등록 성공")
    void 신고_등록_성공() {
        User user = new User();
        user.setUsername("user1");
        user.setPassword("pw");
        userRepository.save(user);

        Post post = new Post();
        post.setTitle("신고 테스트");
        post.setContent("내용");
        post.setAuthor(user);
        postRepository.save(post);

        reportService.create(post.getId(), user);

        List<Report> reports = reportRepository.findAll();
        assertThat(reports).hasSize(1);
        assertThat(reports.get(0).getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("중복 신고 예외 발생")
    void 중복_신고_예외() {
        //Given
        User user = new User();
        user.setUsername("user1");
        user.setPassword("pw");
        userRepository.save(user);

        Post post = new Post();
        post.setTitle("신고 테스트");
        post.setContent("내용");
        post.setAuthor(user);
        postRepository.save(post);

        //When
        reportService.create(post.getId(), user);

        //Then
        assertThatThrownBy(() -> reportService.create(post.getId(), user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 신고한 게시물입니다.");
    }

    @Test
    @DisplayName("신고 5건 이상 게시글 조회 성공")
    void 다수_신고_게시글_조회() {
        //Given
        Post post = new Post();
        post.setTitle("다수신고");
        post.setContent("내용");
        User author = new User();
        author.setUsername("author");
        author.setPassword("pw");
        userRepository.save(author);
        post.setAuthor(author);
        postRepository.save(post);

        for (int i = 1; i <= 5; i++) {
            User user = new User();
            user.setUsername("user" + i);
            user.setPassword("pw");
            userRepository.save(user);

            Report report = new Report(user, post);
            reportRepository.save(report);
        }
        //When
        List<Post> result = reportService.findByPostsWithMoreThanFiveReports();
        //Then
        assertThat(result).contains(post);
    }

    @Test
    @DisplayName("신고 전체 조회")
    void 전체_신고_조회() {
        //Given
        User user = new User();
        user.setUsername("user1");
        user.setPassword("pw");
        userRepository.save(user);

        Post post = new Post();
        post.setTitle("신고 테스트");
        post.setContent("내용");
        post.setAuthor(user);
        postRepository.save(post);

        Report report = new Report(user, post);
        reportRepository.save(report);

        //When
        List<Report> reports = reportService.findAll();
        //Then
        assertThat(reports).hasSize(1);
        assertThat(reports.get(0).getPost()).isEqualTo(post);
    }
}
