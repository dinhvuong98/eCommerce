package com.example.demo.ControllerTest;

import com.example.demo.Utils.TestUtils;
import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartTest {
    private CartController cartController;
    private final CartRepository cartRepository = mock(CartRepository.class);

    private final ItemRepository itemRepository = mock(ItemRepository.class);

    private final UserRepository userRepository = mock(UserRepository.class);

    @Before
    public void before() throws NoSuchFieldException, IllegalAccessException {
        cartController = new CartController();
        TestUtils.injectObject(cartController, "cartRepository", cartRepository);
        TestUtils.injectObject(cartController, "itemRepository", itemRepository);
        TestUtils.injectObject(cartController, "userRepository", userRepository);
    }

    @Test
    public void addTocartTest() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("dinhvuong");
        user.setPassword("1234567");

        Item item = new Item();
        item.setId(1L);
        item.setName("Item1");
        item.setDescription("Description item 1");
        item.setPrice(BigDecimal.valueOf(100));

        Cart cart = new Cart();
        cart.setUser(user);
        user.setCart(cart);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(item.getId());
        request.setQuantity(5);
        request.setUsername(user.getUsername());

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));


        BigDecimal afterTotal = item.getPrice().multiply(new BigDecimal(request.getQuantity()));

        final ResponseEntity<Cart> response = cartController.addTocart(request);

        Cart cartResponse = response.getBody();

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());

        assert cartResponse != null;
        assertEquals(cartResponse.getTotal(), afterTotal);
        assertEquals(cartResponse.getUser(), user);
        assertTrue(cartResponse.getItems().contains(item));
    }

    @Test
    public void removeFromcartTest() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("dinhvuong");
        user.setPassword("1234567");

        Item item = new Item();
        item.setId(1L);
        item.setName("Item1");
        item.setDescription("Description item 1");
        item.setPrice(BigDecimal.valueOf(100));

        Cart cart = new Cart();
        cart.setUser(user);
        user.setCart(cart);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(item.getId());
        request.setQuantity(5);
        request.setUsername(user.getUsername());

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        BigDecimal afterTotal = item.getPrice().multiply(new BigDecimal(request.getQuantity()));

        cartController.addTocart(request);

        final ResponseEntity<Cart> removeCart = cartController.removeFromcart(request);

        Cart cartResponse = removeCart.getBody();

        assertNotNull(removeCart);
        assertEquals(HttpStatus.OK.value(), removeCart.getStatusCodeValue());
        assertNotNull(cartResponse);
        assertNotEquals(cartResponse.getTotal(), afterTotal);
        assertEquals(cartResponse.getTotal(), new BigDecimal(0));
        assertEquals(cartResponse.getUser(), user);
        assertEquals(cartResponse.getItems().size(), 0);
    }
}
