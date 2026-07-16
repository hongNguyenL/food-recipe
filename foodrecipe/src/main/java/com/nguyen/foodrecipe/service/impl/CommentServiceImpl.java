package com.nguyen.foodrecipe.service.impl;

import com.nguyen.foodrecipe.dto.CommentRequest;
import com.nguyen.foodrecipe.dto.CommentResponse;
import com.nguyen.foodrecipe.entity.Comment;
import com.nguyen.foodrecipe.entity.Recipe;
import com.nguyen.foodrecipe.entity.User;
import com.nguyen.foodrecipe.exception.CommentNotFoundException;
import com.nguyen.foodrecipe.exception.RecipeNotFoundException;
import com.nguyen.foodrecipe.exception.UnauthorizedModificationException;
import com.nguyen.foodrecipe.mapper.CommentMapper;
import com.nguyen.foodrecipe.repository.CommentRepository;
import com.nguyen.foodrecipe.repository.RecipeRepository;
import com.nguyen.foodrecipe.repository.UserRepository;
import com.nguyen.foodrecipe.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public CommentResponse createComment(Long userId, Long recipeId, CommentRequest request) {
        log.debug("Creating comment: user={}, recipe={}", userId, recipeId);
        if (!recipeRepository.existsById(recipeId)) {
            throw new RecipeNotFoundException(recipeId);
        }
        User user = userRepository.getReferenceById(userId);
        Recipe recipe = recipeRepository.getReferenceById(recipeId);
        Comment comment = Comment.builder()
                .user(user).recipe(recipe).content(request.content().trim()).build();
        Comment saved = commentRepository.save(comment);
        log.info("Comment created: id={}, user={}, recipe={}", saved.getId(), userId, recipeId);
        return commentMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CommentResponse updateComment(Long userId, Long commentId, CommentRequest request) {
        log.debug("Updating comment: id={}, user={}", commentId, userId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
        if (!comment.getUser().getId().equals(userId)) {
            throw new UnauthorizedModificationException("You can only edit your own comments");
        }
        comment.setContent(request.content().trim());
        Comment saved = commentRepository.save(comment);
        log.info("Comment updated: id={}", commentId);
        return commentMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId, String userRole) {
        log.debug("Deleting comment: id={}, user={}", commentId, userId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
        boolean isOwner = comment.getUser().getId().equals(userId);
        boolean isAdmin = "ADMIN".equals(userRole);
        if (!isOwner && !isAdmin) {
            throw new UnauthorizedModificationException("You can only delete your own comments");
        }
        commentRepository.delete(comment);
        log.info("Comment deleted: id={}", commentId);
    }

    @Override
    public Page<CommentResponse> getRecipeComments(Long recipeId, Pageable pageable) {
        log.debug("Fetching comments for recipe: {}", recipeId);
        if (!recipeRepository.existsById(recipeId)) {
            throw new RecipeNotFoundException(recipeId);
        }
        return commentRepository.findByRecipeIdOrderByCreatedAtDesc(recipeId, pageable)
                .map(commentMapper::toResponse);
    }
}
