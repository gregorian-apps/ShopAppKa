package com.shop.list.shopappka.services;

import com.shop.list.shopappka.exceptions.ShoppingCartExistsException;
import com.shop.list.shopappka.exceptions.ShoppingCartNotFoundException;
import com.shop.list.shopappka.models.domain.Group;
import com.shop.list.shopappka.models.domain.ShoppingCart;
import com.shop.list.shopappka.repositories.ShoppingCartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceTest {

    public static final String SHOPPING_CART_NAME = "Dummy name";
    public static final String DUMMY_GROUP_NAME = "Dummy group";
    public static final long GROUP_ID = 1L;

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private GroupService groupService;

    @InjectMocks
    private ShoppingCartService shoppingCartService;

    private Group group;
    private ShoppingCart shoppingCart;

    @BeforeEach
    void setup() {
        group = Group.builder().groupId(GROUP_ID).name(DUMMY_GROUP_NAME).build();
        shoppingCart = ShoppingCart.builder().shoppingCartId(10L).shoppingCartName(SHOPPING_CART_NAME).group(group).build();
    }

    @Nested
    @DisplayName("Test cases for addNewShoppingCart() method")
    class addNewShoppingCart {

        @Test
        void shouldAddNewShoppingCartWhenShoppingCartNotFound() {
            when(shoppingCartRepository.findShoppingCartByName(anyString())).thenReturn(Optional.empty());
            when(groupService.getGroupById(anyLong())).thenReturn(group);
            when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(shoppingCart);

            ShoppingCart addedShoppingCart = shoppingCartService.addNewShoppingCart(SHOPPING_CART_NAME, GROUP_ID);

            verify(groupService).getGroupById(anyLong());
            verify(shoppingCartRepository).findShoppingCartByName(anyString());
            assertAll(
                    () -> assertEquals(shoppingCart.getShoppingCartId(), addedShoppingCart.getShoppingCartId()),
                    () -> assertEquals(shoppingCart.getShoppingCartName(), addedShoppingCart.getShoppingCartName()),
                    () -> assertEquals(group, addedShoppingCart.getGroup())
            );
        }

        @Test
        void shouldThrownShoppingCartExistsExceptionWhenShoppingCartExists() {
            when(shoppingCartRepository.findShoppingCartByName(anyString())).thenReturn(Optional.of(shoppingCart));
            assertThrows(ShoppingCartExistsException.class, () -> shoppingCartService.addNewShoppingCart("Dummy name", 1L));
        }
    }

    @Nested
    @DisplayName("Test cases for updateNameOfShoppingCart() method")
    class updateNameOfShoppingName {

        @Test
        void shouldUpdateNameOfShoppingCartWhenShoppingCartExists() {
            when(shoppingCartRepository.findShoppingCartByShoppingCartId(anyLong())).thenReturn(Optional.of(shoppingCart));
            when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(shoppingCart);

            ShoppingCart updatedShoppingCart = shoppingCartService.updateNameOfShoppingCart(anyLong(), "Dummy update name");

            assertAll(
                    () -> assertNotNull(updatedShoppingCart),
                    () -> assertEquals("Dummy update name", updatedShoppingCart.getShoppingCartName())
            );
        }

        @Test
        void shouldThrownShoppingCartNotFoundExceptionWhenUpdatedShoppingCartNotFound() {
            when(shoppingCartRepository.findShoppingCartByShoppingCartId(anyLong())).thenReturn(Optional.empty());
            assertThrows(ShoppingCartNotFoundException.class, () -> shoppingCartService.updateNameOfShoppingCart(1L,"Dummy"));
        }
    }

    @Test
    void shouldDeleteShoppingCartWhenShoppingCartExists() {
        when(shoppingCartRepository.findShoppingCartByShoppingCartId(anyLong())).thenReturn(Optional.of(shoppingCart));
        shoppingCartService.deleteShoppingCartById(1L);
        verify(shoppingCartRepository).delete(any(ShoppingCart.class));
    }

}