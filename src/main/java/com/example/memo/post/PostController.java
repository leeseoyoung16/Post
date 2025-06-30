package com.example.memo.post;

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
@RequestMapping("/post")
public class PostController
{
    private final PostService postService;

    @GetMapping("/{id}")
    public PostResponse findById(@PathVariable Long id) {
        Post post = postService.findById(id);
        return new PostResponse(post);
    }

    @GetMapping()
    public List<PostResponse> findAll() {
        return postService.findAll().stream()
                .map(PostResponse::new)
                .toList();
    }

    @GetMapping("/paged")
    public Page<PostResponse> findAllPaged(
        @PageableDefault(size = 5, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return postService.findAllPaged(pageable)
                .map(PostResponse::new);
    }

    @GetMapping("/search")
    public List<PostResponse> findByTitleContaining(@RequestParam(required = false) String keyword) {
        if(keyword == null || keyword.trim().isEmpty()) {
            return postService.findAll().stream()
                    .map(PostResponse::new)
                    .toList();
        }
        return postService.findByTitleContaining(keyword).stream()
                .map(PostResponse::new)
                .toList();
    }

    @PostMapping()
    public ResponseEntity<Void> create(@Valid @RequestBody PostRequest postRequest) {
        postService.create(postRequest.getTitle(), postRequest.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        postService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id,@Valid @RequestBody PostRequest postRequest) {
        postService.update(id, postRequest.getTitle(), postRequest.getContent());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
