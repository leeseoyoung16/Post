package com.example.memo.report;

import com.example.memo.post.Post;
import com.example.memo.post.PostRepository;
import com.example.memo.post.PostResponse;
import com.example.memo.user.User;
import com.example.memo.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService
{
    private final ReportRepository reportRepository;
    private final PostRepository postRepository;

    //신고
    @Transactional
    public void create(Long postId, User user)
    {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));

        if (reportRepository.findByUserAndPost(user, post).isPresent()) {
            throw new IllegalArgumentException("이미 신고한 게시물입니다.");
        }
        Report report = new Report(user, post);
        reportRepository.save(report);
    }

    //신고 전체 리스트 조회 (admin)
    public List<Report> findAll() {
        return reportRepository.findAll();
    }
    //신고 5개 이상인 게시물 조회(admin)
    public List<Post> findByPostsWithMoreThanFiveReports() {
        return reportRepository.findPostsWithMoreThanFiveReports();
    }
}
