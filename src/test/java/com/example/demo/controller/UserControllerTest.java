package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@ActiveProfiles("test")
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private List<User> userList;
    
    @BeforeEach
    void setUp() {
        this.userList = new ArrayList<>();
        this.userList.add(new User(1L, "giangnt@gmail.com", "123456", "GiangNT"));
        this.userList.add(new User(2L, "nhipt@gmail.com", "123456", "NhiPT"));
        this.userList.add(new User(3L, "linhntk@gmail.com", "123456", "LinhNTK"));
        
        objectMapper.registerModule(new ProblemModule());
        objectMapper.registerModule(new ConstraintViolationProblemModule());
    }
    
    @DisplayName("Fetch all users")
    @Test
    void shouldFetchAllUsers() throws Exception {
        
        given(userService.findAllUsers()).willReturn(userList);
        
        this.mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size()", is(userList.size())));
    }
    
    @DisplayName("Fetch user by id")
    @Test
    void shouldFetchOneUserById() throws Exception {
        final Long userId = 1L;
        final User user = new User(1L, "giangnt@gmail.com", "123456", "GiangNT");
        
        given(userService.findUserById(userId)).willReturn(Optional.of(user));
        
        this.mockMvc.perform(get("/api/users/{id}", userId))
            .andExpect(status().isOk())
            .andExpect((ResultMatcher) jsonPath("$.email", is(user.getEmail())))
            .andExpect((ResultMatcher) jsonPath("$.name", is(user.getName())));
    }
    
    @DisplayName("Return 404 when find user by id")
    @Test
    void shouldReturn404WhenFindUserById() throws Exception {
        
        final Long userId = 1L;
        given(userService.findUserById(userId)).willReturn(Optional.empty());
        
        this.mockMvc.perform(get("/api/user/{id}", userId))
            .andExpect(status().isNotFound());
    }
    
    @DisplayName("Create new user")
    @Test
    void shouldCreateNewUser() throws Exception {
        given(userService.createUser(any(User.class))).willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        
        User user = new User(null, "newuser@gmail.com", "123456", "New User");
        
        this.mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(objectMapper.writeValueAsString(user)))
            .andExpect(jsonPath("$.email", is(user.getEmail())))
            .andExpect(jsonPath("$.password", is(user.getPassword())))
            .andExpect(jsonPath("$.name", is(user.getName())));
    }
    
    @DisplayName("Return 404 when update non existing user")
    @Test
    void shouldReturn404WhenUpdatingNonExistingUser() throws Exception {
        Long userId = 1L;
        given(userService.findUserById(userId)).willReturn(Optional.empty());
        
        User user = new User(userId, "user1@gmail.com", "pwd", "Name");
        
        this.mockMvc.perform(put("/api/users/{id}", userId)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isNotFound());
    }
    
    @DisplayName("Update user")
    @Test
    void shouldUpdateUser() throws Exception {
        Long userId = 1L;
        User user = new User(userId, "user1@gmail.com", "pwd", "Name");
        given(userService.findUserById(userId)).willReturn(Optional.of(user));
        given(userService.updateUser(any(User.class))).willAnswer((invocation) -> invocation.getArgument(0));
        
        this.mockMvc.perform(put("/api/users/{id}", user.getId())
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email", is(user.getEmail())))
            .andExpect(jsonPath("$.password", is(user.getPassword())))
            .andExpect(jsonPath("$.name", is(user.getName())));
    }
    
    @DisplayName("Delete user")
    @Test
    void shouldDeleteUser() throws Exception {
        
        Long userId = 1L;
        User user = new User(userId, "user1@gmail.com", "123456", "User1");
        
        given(userService.findUserById(userId)).willReturn(Optional.of(user));
        
        doNothing().when(userService).deleteUserById(user.getId());
        
        this.mockMvc.perform(delete("/api/users/{id}", user.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email", is(user.getEmail())))
            .andExpect(jsonPath("$.password", is(user.getPassword())))
            .andExpect(jsonPath("$.name", is(user.getName())));
    }
    
    @DisplayName("Return 404 when delete none existing user")
    @Test
    void shouldReturn404WhenDeletingNonExistingUser() throws Exception {
        Long userId = 1L;
        given(userService.findUserById(userId)).willReturn(Optional.empty());
        
        this.mockMvc.perform(delete("/api/users/{id}", userId))
            .andExpect(status().isNotFound());
    }
}