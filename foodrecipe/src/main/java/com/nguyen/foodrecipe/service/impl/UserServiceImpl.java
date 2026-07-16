package com.nguyen.foodrecipe.service.impl;

import com.nguyen.foodrecipe.dto.UserResponse;
import com.nguyen.foodrecipe.entity.User;
import com.nguyen.foodrecipe.exception.RecipeNotFoundException;
import com.nguyen.foodrecipe.mapper.UserMapper;
import com.nguyen.foodrecipe.repository.UserRepository;
import com.nguyen.foodrecipe.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse getCurrentUser(Long userId) {
        log.debug("Fetching current user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RecipeNotFoundException("User not found with id: " + userId));

        return userMapper.toResponse(user);
    }
}
