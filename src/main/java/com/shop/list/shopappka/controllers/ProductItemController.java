package com.shop.list.shopappka.controllers;

import com.shop.list.shopappka.models.domain.ProductItem;
import com.shop.list.shopappka.payload.ProductItemRequest;
import com.shop.list.shopappka.services.MapValidationErrorService;
import com.shop.list.shopappka.services.ProductItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/data/groups/{groupId}/shoppingCarts/{shoppingCartId}")
public class ProductItemController {

    private final ProductItemService productItemService;

    private final MapValidationErrorService mapValidationErrorService;

    public ProductItemController(ProductItemService productItemService, MapValidationErrorService mapValidationErrorService) {
        this.productItemService = productItemService;
        this.mapValidationErrorService = mapValidationErrorService;
    }

    @PutMapping("products/{productId}")
    public ResponseEntity<?> updateProductInShoppingCart(@PathVariable Long shoppingCartId,
                                                         @PathVariable Long productId,
                                                         @RequestBody ProductItemRequest updateProductItem,
                                                         BindingResult result) {
        ResponseEntity<?> errorMap = mapValidationErrorService.mapValidationError(result);
        if(errorMap != null) {
            return errorMap;
        }

        ProductItem updatedProductItem = productItemService.updateProductItemInShoppingCart(shoppingCartId, productId, updateProductItem);
        return new ResponseEntity<>(updatedProductItem, HttpStatus.OK);
    }

    @GetMapping("products/{productId}")
    public ResponseEntity<?> getProductFromShoppingCart(@PathVariable Long shoppingCartId, @PathVariable Long productId) {
        ProductItem productItem = productItemService.getProductItemByIdFromShoppingCart(shoppingCartId, productId);
        return new ResponseEntity<>(productItem, HttpStatus.OK);
    }

    @GetMapping("products")
    public ResponseEntity<List<ProductItem>> getAllProductsFromShoppingCart(@PathVariable Long shoppingCartId) {
        List<ProductItem> productItems = productItemService.getAllProductItemsByShoppingCartId(shoppingCartId);
        return new ResponseEntity<>(productItems, HttpStatus.OK);
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<?> deleteProductFromShoppingCart(@PathVariable Long shoppingCartId, @PathVariable Long productId) {
        productItemService.deleteProductItemFromShoppingCart(shoppingCartId, productId);
        return new ResponseEntity<>("Product with id: " + productId + "has been deleted from shopping cart", HttpStatus.OK);
    }
}
