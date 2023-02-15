package com.shop.list.shopappka.services;

import com.shop.list.shopappka.exceptions.ProductItemNotFoundException;
import com.shop.list.shopappka.models.domain.ProductItem;
import com.shop.list.shopappka.models.domain.ProductType;
import com.shop.list.shopappka.models.domain.ShoppingCart;
import com.shop.list.shopappka.payload.ProductItemRequest;
import com.shop.list.shopappka.repositories.ProductItemRepository;
import com.shop.list.shopappka.repositories.ShoppingCartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductItemServiceTest {

    @Mock
    private ProductItemRepository productItemRepository;

    @Mock
    private ShoppingCartService shoppingCartService;

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @InjectMocks
    private ProductItemService productItemService;

    private ShoppingCart shoppingCart;
    private ProductItem productItem;
    private ProductItem productItem1;
    private ProductItem productItem2;
    private ProductItemRequest productItemRequest;

    private List<ProductItem> productItems;

    @BeforeEach
    void setup() {
        shoppingCart = ShoppingCart.builder().shoppingCartId(1L).shoppingCartName("Dummy name").group(null).build();
        productItem = ProductItem.builder().productId(2L).productType(ProductType.CLEANING).productName("Clean name").amount(2).shoppingCart(shoppingCart).build();
        productItem1 = ProductItem.builder().productId(3L).productType(ProductType.SWEET).productName("Cakes").amount(3).shoppingCart(shoppingCart).build();
        productItem2 = ProductItem.builder().productId(4L).productType(ProductType.BREADS).productName("Flour").amount(4).shoppingCart(shoppingCart).build();
        productItemRequest = ProductItemRequest.builder().productType(ProductType.CLEANING).productName("Clean name").amount(2).build();
        productItems = List.of(productItem, productItem1, productItem2);
    }

    @Nested
    @DisplayName("Test cases for addProductItemsToShoppingCart() method")
    class addProductItems {

        @Test
        void shouldAddProductItemsToShoppingCartWhenShoppingCartNotFoundThenReturnListOfProductItems() {
            ProductItemRequest productItemRequest1 = ProductItemRequest.builder().productType(ProductType.SWEET).productName("Cakes").amount(3).build();
            ProductItemRequest productItemRequest2 = ProductItemRequest.builder().productType(ProductType.BREADS).productName("Flour").amount(4).build();
            List<ProductItemRequest> productItemRequestList = List.of(productItemRequest, productItemRequest1, productItemRequest2);
            when(shoppingCartRepository.findShoppingCartByName(anyString())).thenReturn(Optional.empty());
            when(shoppingCartService.addNewShoppingCart(anyString(), anyLong())).thenReturn(shoppingCart);
            when(productItemRepository.saveAll(anyList())).thenReturn(productItems);

            List<ProductItem> addedProductItems = productItemService.addProductItemsToShoppingCart(1L, "Dummy name", productItemRequestList);

            verify(shoppingCartRepository).findShoppingCartByName(anyString());
            verify(shoppingCartService).addNewShoppingCart(anyString(), anyLong());

            assertAll(
                    () -> assertNotNull(addedProductItems),
                    () -> assertEquals(productItems.size(), addedProductItems.size()),
                    () -> checkIfEachProductIncludesOfIndicateAboutShoppingCart(addedProductItems, shoppingCart)
            );
        }

        private void checkIfEachProductIncludesOfIndicateAboutShoppingCart(List<ProductItem> productItems, ShoppingCart shoppingCart) {
            productItems.forEach(item -> assertEquals(item.getShoppingCart(), shoppingCart));
        }
    }

    @Nested
    @DisplayName("Test cases for getProductItemByIdFromShoppingCart() method")
    class getProductItemByIdFromShoppingCart {

        @Test
        void shouldReturnProductItemWhenProductItemExistsInTheShoppingCart() {
            when(productItemRepository.findAllByShoppingCart(anyLong())).thenReturn(productItems);

            ProductItem productItemFromShoppingCart = productItemService.getProductItemByIdFromShoppingCart(1L, 3L);

            assertAll(
                    () -> assertNotNull(productItemFromShoppingCart),
                    () -> assertEquals(3L, productItemFromShoppingCart.getProductId()),
                    () -> assertEquals("Cakes", productItemFromShoppingCart.getProductName()),
                    () -> assertEquals(ProductType.SWEET, productItemFromShoppingCart.getProductType()),
                    () -> assertEquals(3, productItemFromShoppingCart.getAmount()),
                    () -> assertEquals(1L, productItemFromShoppingCart.getShoppingCart().getShoppingCartId())
            );
        }

        @Test
        void shouldThrownProductItemNotFoundExceptionWhenProductItemNotFoundInShoppingCart() {
            when(productItemRepository.findAllByShoppingCart(anyLong())).thenReturn(productItems);
            assertThrows(ProductItemNotFoundException.class, () -> productItemService.getProductItemByIdFromShoppingCart(1L, 5L));
        }
    }

    @Nested
    @DisplayName("Test cases for updateProductItemInShoppingCart() method")
    class updateProductItemInShoppingCart {
        @Test
        void shouldReturnUpdatedProductItemWhenProductItemExistsInShoppingCart() {
            ProductItemRequest productItemToUpdate = ProductItemRequest.builder()
                    .productName("Dummy name")
                    .amount(10)
                    .productType(ProductType.CLEANING)
                    .build();
            when(productItemRepository.findAllByShoppingCart(anyLong())).thenReturn(productItems);
            when(productItemRepository.save(productItem)).thenReturn(productItem);

            ProductItem updatedProductItem = productItemService.updateProductItemInShoppingCart(1L, 2L, productItemToUpdate);

            verify(productItemRepository).save(any());
            verify(productItemRepository).findAllByShoppingCart(anyLong());
            assertAll(
                    () -> assertNotNull(updatedProductItem),
                    () -> assertEquals(productItemToUpdate.getProductName(), updatedProductItem.getProductName()),
                    () -> assertEquals(productItemToUpdate.getAmount(), updatedProductItem.getAmount()),
                    () -> assertEquals(productItemToUpdate.getProductType(), updatedProductItem.getProductType())
            );
        }

        @Test
        void shouldReturnNullPointerExceptionWhenProductItemToUpdateIsNull() {
            ProductItemRequest productItemToUpdate = null;
            when(productItemRepository.findAllByShoppingCart(anyLong())).thenReturn(productItems);
            assertThrows(NullPointerException.class, () -> productItemService.updateProductItemInShoppingCart(1L, 2L, productItemToUpdate));
        }
    }

    @Test
    void shouldDeleteProductFromShoppingCartWhenDeletedProductExists() {
        when(productItemRepository.findAllByShoppingCart(anyLong())).thenReturn(productItems);
        productItemService.deleteProductItemFromShoppingCart(1L, 4L);
        verify(productItemRepository).delete(any(ProductItem.class));
    }

    @Test
    void shouldDeleteAllProductItemsFromShoppingCartWhenShoppingCartHasProductItems() {
        when(productItemRepository.findAllByShoppingCart(anyLong())).thenReturn(productItems);
        productItemService.deleteAllProductItemFromShoppingCart(1L);
        verify(productItemRepository).deleteAll(anyList());
    }
}