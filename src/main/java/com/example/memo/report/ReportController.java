package com.example.memo.report;

import com.example.memo.post.Post;
import com.example.memo.post.PostResponse;
import com.example.memo.user.CustomUserDetails;
import com.example.memo.user.User;
import com.example.memo.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReportController
{
    private final ReportService reportService;

    @PostMapping("/posts/{postid}/reports")
    public ResponseEntity<Void> create(@PathVariable Long postid,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        reportService.create(postid, userDetails.getUser());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/reports")
    public ResponseEntity<List<ReportResponse>> findAll(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ReportResponse> reports = reportService.findAll().stream()
                .map(ReportResponse::new)
                .toList();
        return ResponseEntity.ok(reports);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/reports/danger-posts")
    public ResponseEntity<List<PostResponse>> findByPostsWithMoreThanFiveReports (@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<PostResponse> posts = reportService.findByPostsWithMoreThanFiveReports().stream()
                .map(PostResponse::new)
                .toList();
        return ResponseEntity.ok(posts);
    }
}
