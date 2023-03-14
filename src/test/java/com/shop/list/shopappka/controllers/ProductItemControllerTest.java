package com.shop.list.shopappka.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.list.shopappka.configurations.auth.TokenProvider;
import com.shop.list.shopappka.exceptions.ProductItemNotFoundException;
import com.shop.list.shopappka.models.domain.ProductItem;
import com.shop.list.shopappka.models.domain.ProductType;
import com.shop.list.shopappka.payload.ProductItemRequest;
import com.shop.list.shopappka.services.JwtUserService;
import com.shop.list.shopappka.services.MapValidationErrorService;
import com.shop.list.shopappka.services.ProductItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ProductItemController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ProductItemControllerTest {

    private static final String API_URL = "/api/data/groups/{groupId}/shoppingCarts/{shoppingCartId}/products";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private MapValidationErrorService mapValidationErrorService;

    @MockBean
    private ProductItemService productItemService;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private JwtUserService jwtUserService;

    private final ObjectMapper mapper = new ObjectMapper();

    private ProductItemRequest productItemRequest;
    private ProductItem productItem1;
    private ProductItem productItem2;
    private List<ProductItem> productItems;

    @BeforeEach
    void setup() {
        productItemRequest = ProductItemRequest.builder().productName("dummy product name2").amount(2).productType(ProductType.BREADS).build();
        productItem1 = ProductItem.builder().productId(1L).productName("dummy product name1").amount(1).productType(ProductType.CHEMISTRY).build();
        productItem2 = ProductItem.builder().productId(2L).productName("dummy product name2").amount(2).productType(ProductType.BREADS).build();
        productItems = List.of(productItem1, productItem2);
    }

    @Test
    void shouldUpdateProductInShoppingCartWhenProductItemExistsThenReturnUpdatedProductItemWithStatusCode200() throws Exception {
        productItem1.setProductName(productItemRequest.getProductName());
        productItem1.setProductType(productItemRequest.getProductType());
        productItem1.setAmount(productItemRequest.getAmount());
        when(mapValidationErrorService.mapValidationError(any())).thenReturn(null);
        when(productItemService.updateProductItemInShoppingCart(anyLong(), anyLong(), any())).thenReturn(productItem1);
        mvc.perform(MockMvcRequestBuilders.put(API_URL + "/{productId}", 1L, 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(productItemRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.productName").value(productItemRequest.getProductName()))
                .andExpect(jsonPath("$.productType").value(productItemRequest.getProductType().toString()))
                .andExpect(jsonPath("$.amount").value(productItemRequest.getAmount()));
    }

    @Test
    void shouldThrownProductItemNotFoundExceptionWhenProductItemNotFoundThenReturnStatusCode404() throws Exception {
        when(productItemService.updateProductItemInShoppingCart(anyLong(), anyLong(), any())).thenThrow(ProductItemNotFoundException.class);
        mvc.perform(MockMvcRequestBuilders.put(API_URL + "/{productId}", 1L, 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(productItemRequest)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetProductItemByIdWhenProductItemExistsThenReturnProductItemWithStatusCode200() throws Exception {
        when(productItemService.getProductItemByIdFromShoppingCart(anyLong(), anyLong())).thenReturn(productItem1);
        mvc.perform(MockMvcRequestBuilders.get(API_URL + "/{productId}", 1L, 1L, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.productId").value(productItem1.getProductId()))
                .andExpect(jsonPath("$.productName").value(productItem1.getProductName()))
                .andExpect(jsonPath("$.productType").value(productItem1.getProductType().toString()))
                .andExpect(jsonPath("$.amount").value(productItem1.getAmount()));
    }

    @Test
    void shouldGetAllProductsFromShoppingCartThenReturnListOfProductItemsWithStatusCode200() throws Exception {
        when(productItemService.getAllProductItemsByShoppingCartId(anyLong())).thenReturn(productItems);
        mvc.perform(MockMvcRequestBuilders.get(API_URL, 1L, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    void shouldDeleteProductItemFromShoppingCartWhenProductItemExistsThenReturnInformationAboutDeletionWithStatusCode200() throws Exception {
        doNothing().when(productItemService).deleteProductItemFromShoppingCart(anyLong(), anyLong());
        mvc.perform(MockMvcRequestBuilders.delete(API_URL + "/{productId}", 1L, 1L, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Product with id: 1 has been deleted from shopping cart"));
    }

}