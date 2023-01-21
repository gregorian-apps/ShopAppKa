package com.shop.list.shopappka.services;


import com.shop.list.shopappka.exceptions.ProductItemNotFoundException;
import com.shop.list.shopappka.models.domain.ProductItem;
import com.shop.list.shopappka.models.domain.ShoppingCart;
import com.shop.list.shopappka.repositories.ProductItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class ProductItemService {

    private final ProductItemRepository productItemRepository;

    private final ShoppingCartService shoppingCartService;

    public ProductItemService(ProductItemRepository productItemRepository, ShoppingCartService shoppingCartService) {
        this.productItemRepository = productItemRepository;
        this.shoppingCartService = shoppingCartService;
    }

    public List<ProductItem> addProductItemsToShoppingCart(String shoppingCartName, List<ProductItem> productItems) {
        ShoppingCart shoppingCart = shoppingCartService.addNewShoppingCart(shoppingCartName);
        for (ProductItem item : productItems) {
            item.setShoppingCart(shoppingCart);
        }
        log.info("Added new products to shopping Cart with name {} successfully", shoppingCartName);
        return productItemRepository.saveAll(productItems);
    }

    /*
     * @TODO Add update method for product item in shopping cart
     *   */

    public void deleteProductItemFromShoppingCart(Long shoppingCartId, Long productId) {
        ProductItem productItem = getProductItemByIdFromShoppingCart(shoppingCartId, productId);
        log.info("Product item with id {} from shopping cart {} has deleted successfully", productId, shoppingCartId);
        productItemRepository.delete(productItem);
    }

    public ProductItem getProductItemByIdFromShoppingCart(Long shoppingCartId, Long productId) {
        return getAllProductItemsByShoppingCartId(shoppingCartId).stream()
                .filter(productItem -> Objects.equals(productItem.getProductId(), productId))
                .findFirst().orElseThrow(() -> {
                    log.error("Product item with id {} not found in shopping cart with id {}", productId, shoppingCartId);
                    throw new ProductItemNotFoundException("Product item with id: " + productId + " not found in Shopping Cart with id: " + shoppingCartId);
                });
    }

    public void deleteAllProductItemFromShoppingCart(Long shoppingCartId) {
        List<ProductItem> productItems = getAllProductItemsByShoppingCartId(shoppingCartId);
        log.info("All product items has deleted successfully from shopping cart {}", shoppingCartId);
        productItemRepository.deleteAll(productItems);
    }

    public List<ProductItem> getAllProductItemsByShoppingCartId(Long shoppingCartId) {
        return productItemRepository.findAllByShoppingCart(shoppingCartId);
    }

}
