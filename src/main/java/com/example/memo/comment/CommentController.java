package com.example.memo.comment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/comment")
public class CommentController
{
    private final CommentService commentService;

    @PostMapping("/post/{postId}")
    public ResponseEntity<Void> create(@PathVariable Long postId, @Valid @RequestBody CommentRequest commentRequest) {
        commentService.create(postId, commentRequest.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/post/{postId}")
    public List<CommmetResponse> findAll(@PathVariable Long postId) {
        return commentService.findAll(postId).stream()
                .map(CommmetResponse::new)
                .toList();
    }

    @GetMapping("/post/{postId}/paged")
    public Page<CommmetResponse> findAllPaged(@PathVariable Long postId,
                                              @PageableDefault(size = 5, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return commentService.findAllPaged(postId, pageable)
                .map(CommmetResponse::new);
    }

    @GetMapping("/{id}")
    public CommmetResponse findById(@PathVariable Long id) {
        Comment comment = commentService.findById(id);
        return new CommmetResponse(comment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @Valid @RequestBody CommentRequest commentRequest) {
        commentService.update(id, commentRequest.getContent());
        return ResponseEntity.ok().build();
    }

}
