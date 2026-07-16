package com.nguyen.foodrecipe.controller;

import com.nguyen.foodrecipe.dto.ApiResponse;
import com.nguyen.foodrecipe.dto.CommentRequest;
import com.nguyen.foodrecipe.dto.CommentResponse;
import com.nguyen.foodrecipe.security.UserPrincipal;
import com.nguyen.foodrecipe.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "Comment", description = "Comment management APIs")
public class CommentController {

    private final CommentService commentService;

    @PutMapping("/{id}")
    @Operation(summary = "Update comment", description = "Edit your own comment. Only the comment author may edit.")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        CommentResponse response = commentService.updateComment(userPrincipal.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Comment updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete comment", description = "Delete your own comment. Admins may delete any comment.")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        commentService.deleteComment(userPrincipal.getId(), id, userPrincipal.getRole());
        return ResponseEntity.ok(ApiResponse.success("Comment deleted successfully"));
    }
}
