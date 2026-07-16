package com.nguyen.foodrecipe.service;

import com.nguyen.foodrecipe.dto.CommentRequest;
import com.nguyen.foodrecipe.dto.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    CommentResponse createComment(Long userId, Long recipeId, CommentRequest request);

    CommentResponse updateComment(Long userId, Long commentId, CommentRequest request);

    void deleteComment(Long userId, Long commentId, String userRole);

    Page<CommentResponse> getRecipeComments(Long recipeId, Pageable pageable);

    void adminDeleteComment(Long commentId);

    Page<CommentResponse> getAllComments(String keyword, Pageable pageable);
}
