package com.example.demo.ControllerTest;

import com.example.demo.Utils.TestUtils;
import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemTest {

    private ItemController itemController;

    private final ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void before() throws NoSuchFieldException, IllegalAccessException {
        itemController = new ItemController();
        TestUtils.injectObject(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void getItemByIdTest() throws Exception {
        Item itemDb = new Item();

        itemDb.setId(1L);
        itemDb.setName("Round Widget");
        itemDb.setDescription("A widget that is round");
        itemDb.setPrice(BigDecimal.valueOf(100));

        when(itemRepository.findById(itemDb.getId())).thenReturn(Optional.of(itemDb));
        final ResponseEntity<Item> response = itemController.getItemById(itemDb.getId());

        Item itemRun = response.getBody();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assert itemRun != null;
        assertEquals(itemDb.getName(), itemRun.getName());
        assertEquals(itemDb.getDescription(), itemRun.getDescription());
        assertEquals(itemDb.getPrice(), itemRun.getPrice());
    }

    @Test
    public void getItemByIdFailTest() throws Exception {
        final ResponseEntity<Item> response = itemController.getItemById(2L);
        assertEquals(response.getStatusCodeValue(), 404);
    }

    @Test
    public void getItemsByNameTest() throws Exception {
        String searchText = "Widget";

        ArrayList<Item> listItemDb = getListItemDummy();

        when(itemRepository.findItemsByNameContains(searchText)).thenReturn(listItemDb);
        final ResponseEntity<List<Item>> response = itemController.getItemsByName(searchText);

        List<Item> listItemRun = response.getBody();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        assert listItemRun != null;
        assertEquals(listItemRun.size(), 2);
        assertSame(listItemDb, listItemRun);
    }

    private static ArrayList<Item> getListItemDummy() {
        Item itemDb1 = new Item();
        itemDb1.setId(1L);
        itemDb1.setName("Round Widget");
        itemDb1.setDescription("A widget that is round");
        itemDb1.setPrice(BigDecimal.valueOf(100));

        Item itemDb2 = new Item();

        itemDb2.setId(2L);
        itemDb2.setName("Square Widget");
        itemDb2.setDescription("A widget that is square");
        itemDb2.setPrice(BigDecimal.valueOf(200));

        ArrayList<Item> listItem = new ArrayList<>();
        listItem.add(itemDb1);
        listItem.add(itemDb2);

        return listItem;
    }
}
