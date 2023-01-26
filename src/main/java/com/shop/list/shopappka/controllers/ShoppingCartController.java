package com.shop.list.shopappka.controllers;

import com.shop.list.shopappka.models.domain.ProductItem;
import com.shop.list.shopappka.models.domain.ShoppingCart;
import com.shop.list.shopappka.payload.ShoppingCartRequest;
import com.shop.list.shopappka.services.MapValidationErrorService;
import com.shop.list.shopappka.services.ProductItemService;
import com.shop.list.shopappka.services.ShoppingCartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/data/groups/{groupId}/shoppingCarts")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    private final ProductItemService productItemService;

    private final MapValidationErrorService mapValidationErrorService;

    public ShoppingCartController(ShoppingCartService shoppingCartService,
                                  ProductItemService productItemService,
                                  MapValidationErrorService mapValidationErrorService) {
        this.shoppingCartService = shoppingCartService;
        this.productItemService = productItemService;
        this.mapValidationErrorService = mapValidationErrorService;
    }

    @PostMapping("add")
    public ResponseEntity<?> addProductsToShoppingCart(@PathVariable Long groupId,
                                                       @RequestBody ShoppingCartRequest shoppingCartRequest,
                                                       BindingResult result) {
        ResponseEntity<?> errorMap = mapValidationErrorService.mapValidationError(result);
        if (errorMap != null) {
            return errorMap;
        }

        List<ProductItem> addedProductItems = productItemService.addProductItemsToShoppingCart(groupId, shoppingCartRequest.getName(), shoppingCartRequest.getProductItems());
        return new ResponseEntity<>(addedProductItems, HttpStatus.CREATED);

    }

    @PutMapping("{shoppingCartId}")
    public ResponseEntity<?> updateShoppingName(@PathVariable Long shoppingCartId,
                                                @RequestBody ShoppingCartRequest shoppingCartRequest,
                                                BindingResult result) {
        ResponseEntity<?> errorMap = mapValidationErrorService.mapValidationError(result);

        if(errorMap != null) {
            return errorMap;
        }

        ShoppingCart updatedShoppingCart = shoppingCartService.updateNameOfShoppingCart(shoppingCartId, shoppingCartRequest.getName());
        return new ResponseEntity<>(updatedShoppingCart, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ShoppingCart>> getAllShoppingCarts(@PathVariable Long groupId) {
        List<ShoppingCart> shoppingCarts = shoppingCartService.getAllShoppingCartsByGroupId(groupId);
        return new ResponseEntity<>(shoppingCarts, HttpStatus.OK);
    }

    @DeleteMapping("{shoppingId}")
    public ResponseEntity<?> deleteShoppingCart(@PathVariable Long shoppingId) {
        shoppingCartService.deleteShoppingCartById(shoppingId);
        return new ResponseEntity<>("Shopping cart with id: " + shoppingId + " has been deleted", HttpStatus.OK);
    }
}
