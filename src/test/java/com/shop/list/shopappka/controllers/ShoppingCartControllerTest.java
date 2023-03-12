package com.shop.list.shopappka.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.list.shopappka.configurations.auth.TokenProvider;
import com.shop.list.shopappka.exceptions.ShoppingCartNotFoundException;
import com.shop.list.shopappka.models.domain.Group;
import com.shop.list.shopappka.models.domain.ProductItem;
import com.shop.list.shopappka.models.domain.ProductType;
import com.shop.list.shopappka.models.domain.ShoppingCart;
import com.shop.list.shopappka.payload.ProductItemRequest;
import com.shop.list.shopappka.payload.ShoppingCartRequest;
import com.shop.list.shopappka.services.JwtUserService;
import com.shop.list.shopappka.services.MapValidationErrorService;
import com.shop.list.shopappka.services.ProductItemService;
import com.shop.list.shopappka.services.ShoppingCartService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ShoppingCartController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class ShoppingCartControllerTest {

    private final static String API_URL = "/api/data/groups/{groupId}/shoppingCarts";

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ShoppingCartService shoppingCartService;

    @MockBean
    private ProductItemService productItemService;

    @MockBean
    private MapValidationErrorService mapValidationErrorService;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private JwtUserService jwtUserService;

    private ShoppingCartRequest shoppingCartRequest;
    private ShoppingCart shoppingCart;
    private Group group;
    private ProductItemRequest productItemRequest1;
    private ProductItemRequest productItemRequest2;
    private ProductItem productItem1;
    private ProductItem productItem2;

    private List<ProductItemRequest> itemRequestList;
    private List<ProductItem> productItems;

    @BeforeEach
    void setup() {
        group = Group.builder().groupId(1L).name("dummy group").users(null).build();
        shoppingCart = ShoppingCart.builder().shoppingCartId(1L).shoppingCartName("dummy shopping cart name").group(group).build();
        productItemRequest1 = ProductItemRequest.builder().productName("dummy product name1").amount(1).productType(ProductType.CHEMISTRY).build();
        productItemRequest2 = ProductItemRequest.builder().productName("dummy product name2").amount(2).productType(ProductType.BREADS).build();
        itemRequestList = List.of(productItemRequest1, productItemRequest2);
        shoppingCartRequest = ShoppingCartRequest.builder().name("dummy shopping cart name").productItems(itemRequestList).build();
        productItem1 = ProductItem.builder().productId(1L).productName("dummy product name1").amount(1).productType(ProductType.CHEMISTRY).shoppingCart(shoppingCart).build();
        productItem2 = ProductItem.builder().productId(2L).productName("dummy product name2").amount(2).productType(ProductType.BREADS).shoppingCart(shoppingCart).build();
        productItems = List.of(productItem1, productItem2);
    }

    @Test
    void shouldAddProductsToShoppingCartWhenShoppingCartRequestIsValidThenReturnShoppingCartWithAddedProductsAndStatusCode200() throws Exception {
        when(mapValidationErrorService.mapValidationError(any())).thenReturn(null);
        when(productItemService.addProductItemsToShoppingCart(anyLong(), anyString(), anyList())).thenReturn(productItems);
        mvc.perform(MockMvcRequestBuilders.post(API_URL + "/add", 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(shoppingCartRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].productId").value(1L))
                .andExpect(jsonPath("$.[1].productId").value(2L));
    }

    @Test
    void shouldUpdateShoppingNameWhenShoppingCartExistsThenReturnUpdatedShoppingCartWithStatusCode200() throws Exception {
        String updatedShoppingName = "Updated shopping name";
        shoppingCartRequest.setName(updatedShoppingName);
        shoppingCart.setShoppingCartName(updatedShoppingName);
        when(mapValidationErrorService.mapValidationError(any())).thenReturn(null);
        when(shoppingCartService.updateNameOfShoppingCart(anyLong(),anyString())).thenReturn(shoppingCart);
        mvc.perform(MockMvcRequestBuilders.put(API_URL + "/{shoppingCartId}", 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(shoppingCartRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shoppingCartName").value(updatedShoppingName));
    }

    @Test
    void shouldThrownShoppingCartNotFoundExceptionWhenShoppingCartNotFoundThenReturnStatusCode400() throws Exception {
        when(mapValidationErrorService.mapValidationError(any())).thenReturn(null);
        when(shoppingCartService.updateNameOfShoppingCart(anyLong(),anyString())).thenThrow(ShoppingCartNotFoundException.class);
        mvc.perform(MockMvcRequestBuilders.put(API_URL + "/{shoppingCartId}", 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(shoppingCartRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetAllShoppingCartsFromGroupWithStatusCode200() throws Exception {
        ShoppingCart shoppingCart1 = ShoppingCart.builder().shoppingCartId(2L).shoppingCartName("Dummy name").group(group).build();
        when(shoppingCartService.getAllShoppingCartsByGroupId(anyLong())).thenReturn(List.of(shoppingCart, shoppingCart1));
        mvc.perform(MockMvcRequestBuilders.get(API_URL, 1L)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$.[0].shoppingCartName").value("dummy shopping cart name"))
                .andExpect(jsonPath("$.[1].shoppingCartName").value("Dummy name"));
    }

    @Test
    void shouldDeleteShoppingCartWithAllProductsWhenShoppingCartExistsThenReturnInformationAboutDeletionWithStatusCode200() throws Exception {
        doNothing().when(productItemService).deleteAllProductItemFromShoppingCart(anyLong());
        doNothing().when(shoppingCartService).deleteShoppingCartById(anyLong());
        mvc.perform(MockMvcRequestBuilders.delete(API_URL + "/{shoppingCart}", 1L, 1L)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Shopping cart with id: 1 has been deleted with products"));
        verify(shoppingCartService).deleteShoppingCartById(anyLong());
        verify(productItemService).deleteAllProductItemFromShoppingCart(anyLong());
    }
}