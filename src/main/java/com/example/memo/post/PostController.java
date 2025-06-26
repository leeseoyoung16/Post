package com.example.memo.post;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/posts")
public class PostController
{
    private final PostService postService;

    @GetMapping("/{id}")
    public Post findById(@PathVariable Long id) {
        return postService.findById(id);
    }

    @GetMapping()
    public List<Post> findAll() {
        return postService.findAll();
    }

    @GetMapping("/search")
    public List<Post> findByTitleContaining(@RequestParam(required = false) String keyword) {
        if(keyword == null || keyword.trim().isEmpty()) {
            return postService.findAll();
        }
        return postService.findByTitleContaining(keyword);
    }

    @PostMapping()
    public ResponseEntity<Void> create(@Valid @RequestBody PostRequest postRequest) {
        postService.create(postRequest.getTitle(), postRequest.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        postService.delete(id);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id,@Valid @RequestBody PostRequest postRequest) {
        postService.update(id, postRequest.getTitle(), postRequest.getContent());
    }
}
