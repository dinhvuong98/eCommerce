package com.example.demo.ControllerTest;

import com.example.demo.Utils.TestUtils;
import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class OrderTest {

    private OrderController orderController;

    private final UserRepository userRepository = mock(UserRepository.class);

    private final OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void before() throws NoSuchFieldException, IllegalAccessException {
        orderController = new OrderController();
        TestUtils.injectObject(orderController, "userRepository", userRepository);
        TestUtils.injectObject(orderController, "orderRepository", orderRepository);

        User user = new User();
        user.setId(1L);
        user.setUsername("dinhvuong");

        Item item1 = new Item();

        item1.setId(1L);
        item1.setName("Item1");
        item1.setDescription("Description item1");
        item1.setPrice(new BigDecimal(100));

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Item2");
        item2.setDescription("Description item2");
        item2.setPrice(new BigDecimal(200));

        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setItems(items);
        cart.setTotal(item1.getPrice().add(item2.getPrice()));

        user.setCart(cart);

        List<UserOrder> userOrders = new ArrayList<>();

        userOrders.add(UserOrder.createFromCart(user.getCart()));

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(userOrders);
    }

    @Test
    public void submitSuccessTest() throws Exception {
        final ResponseEntity<UserOrder> response = orderController.submit("dinhvuong");

        UserOrder userOrderResponse = response.getBody();

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());

        assertNotNull(userOrderResponse);

        assertEquals(userOrderResponse.getItems().size(), 2);
        assertEquals(userOrderResponse.getUser().getUsername(), "dinhvuong");
        assertNotNull(userOrderResponse.getTotal());

    }

    @Test
    public void submiteFailTest() throws Exception {
        final ResponseEntity<UserOrder> response = orderController.submit("Fail_id");

        UserOrder userOrderResponse = response.getBody();

        assertEquals(response.getStatusCodeValue(), HttpStatus.NOT_FOUND.value());

        assertNotNull(response);
        assertNull(userOrderResponse);
    }

    @Test
    public void submitExceptionTest() throws Exception {
        when(userRepository.findByUsername("")).thenThrow(NullPointerException.class);

        final ResponseEntity<UserOrder> response = orderController.submit("");

        assertEquals(response.getStatusCodeValue(), HttpStatus.BAD_REQUEST.value());
        assertNotNull(response);
    }

    @Test
    public void getOrdersForUserTest() throws Exception {
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("dinhvuong");

        List<UserOrder> userOrdersResponse = response.getBody();

        assertEquals(response.getStatusCodeValue(), HttpStatus.OK.value());

        assertNotNull(response);
        assertNotNull(userOrdersResponse);
        assertEquals(userOrdersResponse.size(), 1);
        assertEquals(userOrdersResponse.get(0).getUser().getUsername(), "dinhvuong");
        assertEquals(userOrdersResponse.get(0).getItems().size(), 2);
        assertNotNull(userOrdersResponse.get(0));
    }

    @Test
    public void getOrdersForUserFailTest() throws Exception {
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("Fail_id");

        List<UserOrder> userOrdersResponse = response.getBody();

        assertEquals(response.getStatusCodeValue(), HttpStatus.NOT_FOUND.value());

        assertNotNull(response);
        assertNull(userOrdersResponse);
    }

}
