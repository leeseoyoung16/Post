package com.example.memo.comment;

import com.example.memo.user.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/comments")
public class CommentController
{
    private final CommentService commentService;

    @PostMapping("/posts/{postId}")
    public ResponseEntity<Void> create(@PathVariable Long postId, @Valid @RequestBody CommentRequest commentRequest,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.create(postId, commentRequest,userDetails.getUser());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @GetMapping()
    public List<CommetResponse> findAll() {
        return commentService.findAll()
                .stream()
                .map(CommetResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/posts/{postId}")
    public List<CommetResponse> findByPost(@PathVariable Long postId) {
        return commentService.findByPost(postId)
                .stream()
                .map(CommetResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/posts/{postId}/paged")
    public Page<CommetResponse> findAllPaged(@PathVariable Long postId,
                                              @PageableDefault(size = 5, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return commentService.findAllPaged(postId, pageable)
                .map(CommetResponse::new);
    }

    @GetMapping("/{id}")
    public CommetResponse findById(@PathVariable Long id) {
        Comment comment = commentService.findById(id);
        return new CommetResponse(comment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.delete(id, userDetails.getUser());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @Valid @RequestBody CommentRequest commentRequest,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.update(id, commentRequest.getContent(), userDetails.getUser());
        return ResponseEntity.ok().build();
    }

}
