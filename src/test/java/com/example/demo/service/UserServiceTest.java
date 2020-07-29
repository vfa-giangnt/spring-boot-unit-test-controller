package com.example.demo.service;

import com.example.demo.exception.UserRegistrationException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    @DisplayName("Save user successful")
    @Test
    void shouldSaveUserSuccessfully() {
        final User user = new User(null, "giangnt@gmail.com", "123456", "GiangNT");
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.empty());
        given(userRepository.save(user)).willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        
        User savedUser = userService.createUser(user);
        
        assertThat(savedUser).isNotNull();
        verify(userRepository).save(any(User.class));
    }
    
    @DisplayName("Error on save user with existing email")
    @Test
    void shouldThrowErrorWhenSaveUserWithExistingEmail() {
        final User user = new User(1L, "giangnt@mail.com", "123456", "GiangNT");
        
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
        
        assertThrows(UserRegistrationException.class, () -> {
            userService.createUser(user);
        });
        
        verify(userRepository, never()).save(any(User.class));
    }
    
    @DisplayName("Update user")
    @Test
    void updateUser() {
        final User user = new User(1L, "giangnt@mail.com", "123456", "GiangNT");
        
        given(userRepository.save(user)).willReturn(user);
        
        final User expected = userService.updateUser(user);
        
        assertThat(expected).isNotNull();
        
        verify(userRepository).save(any(User.class));
    }
    
    @DisplayName("Find all users")
    @Test
    void shouldReturnFindAll() {
        List<User> datas = new ArrayList();
        datas.add(new User(1L, "giangnt@mail.com", "123456", "Gau nau"));
        datas.add(new User(2L, "linhntk@mail.com", "123456", "Linh Kamen"));
        datas.add(new User(3L, "nhipt@mail.com", "123456", "Me Gyro"));
        
        given(userRepository.findAll()).willReturn(datas);
        
        List<User> expected = userService.findAllUsers();
        
        assertEquals(expected, datas);
    }
    
    @DisplayName("Find user by id")
    @Test
    void findUserById() {
        final Long id = 1L;
        final User user = new User(1L, "giangnt@mail.com", "123456", "Gau nau");
        
        given(userRepository.findById(id)).willReturn(Optional.of(user));
        
        final Optional<User> expected = userService.findUserById(id);
        
        assertThat(expected).isNotNull();
        
    }
    
    @DisplayName("Delete user")
    @Test
    void shouldBeDelete() {
        final Long userId = 1L;
        
        userService.deleteUserById(userId);
        userService.deleteUserById(userId);
        
        verify(userRepository, times(2)).deleteById(userId);
    }
}
