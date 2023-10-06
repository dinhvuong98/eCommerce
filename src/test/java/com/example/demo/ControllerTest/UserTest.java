package com.example.demo.ControllerTest;

import com.example.demo.Utils.TestUtils;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserTest {
    private UserController userController;

    private final UserRepository userRepository = mock(UserRepository.class);

    private final CartRepository cartRepository = mock(CartRepository.class);

    private final BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void before() throws NoSuchFieldException, IllegalAccessException {
        userController = new UserController();
        TestUtils.injectObject(userController, "userRepository", userRepository);
        TestUtils.injectObject(userController, "cartRepository", cartRepository);
        TestUtils.injectObject(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }

    @Test
    public void createUserSuccessTest() throws Exception {

        CreateUserRequest requestBody = new CreateUserRequest();
        requestBody.setUsername("dinhvuong1");
        requestBody.setPassword("v123456");
        requestBody.setConfirmPassword("v123456");

        when(bCryptPasswordEncoder.encode("v123456")).thenReturn("encode_password");
        final ResponseEntity<User> response = userController.createUser(requestBody);

        User responseBody = response.getBody();

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertEquals(requestBody.getUsername(), responseBody.getUsername());
        assertEquals("encode_password", responseBody.getPassword());
    }

    @Test
    public void createUserWithPasswordNotMatchTest() throws Exception {

        CreateUserRequest requestBody = new CreateUserRequest();
        requestBody.setUsername("dinhvuong2");
        requestBody.setPassword("v123456");
        requestBody.setConfirmPassword("1234567");

        final ResponseEntity<User> response = userController.createUser(requestBody);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
    }

    @Test
    public void createUserFaillengthPasswordTest() throws Exception {
        CreateUserRequest requestBody = new CreateUserRequest();
        requestBody.setUsername("dinhvuong3");
        requestBody.setPassword("123");
        requestBody.setConfirmPassword("123");

        final ResponseEntity<User> response = userController.createUser(requestBody);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
    }

    @Test
    public void getUserByIdTest() throws Exception {
        User requestBody = new User();
        requestBody.setId(1L);
        requestBody.setUsername("dinhvuong4");

        when(userRepository.findById(requestBody.getId())).thenReturn(Optional.of(requestBody));
        final ResponseEntity<User> response = userController.findById(requestBody.getId());

        User responseBody = response.getBody();

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assert responseBody != null;
        assertEquals(responseBody.getId(), responseBody.getId());
        assertEquals(requestBody.getUsername(), responseBody.getUsername());
    }

    @Test
    public void getUserByUsernametest() throws Exception {
        User requestBody = new User();
        requestBody.setId(2L);
        requestBody.setUsername("dinhvuong5");

        when(userRepository.findByUsername(requestBody.getUsername())).thenReturn(requestBody);
        final ResponseEntity<User> response = userController.findByUserName(requestBody.getUsername());

        User responseBody = response.getBody();

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assert responseBody != null;
        assertEquals(2L, responseBody.getId());
        assertEquals(requestBody.getUsername(), responseBody.getUsername());
    }
}
