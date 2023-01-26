package com.shop.list.shopappka.services;


import com.shop.list.shopappka.exceptions.ProductItemNotFoundException;
import com.shop.list.shopappka.models.domain.ProductItem;
import com.shop.list.shopappka.models.domain.ShoppingCart;
import com.shop.list.shopappka.payload.ProductItemRequest;
import com.shop.list.shopappka.repositories.ProductItemRepository;
import com.shop.list.shopappka.repositories.ShoppingCartRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class ProductItemService {

    private final ProductItemRepository productItemRepository;

    private final ShoppingCartService shoppingCartService;

    private final ShoppingCartRepository shoppingCartRepository;

    public ProductItemService(ProductItemRepository productItemRepository,
                              ShoppingCartService shoppingCartService,
                              ShoppingCartRepository shoppingCartRepository) {
        this.productItemRepository = productItemRepository;
        this.shoppingCartService = shoppingCartService;
        this.shoppingCartRepository = shoppingCartRepository;
    }

    public List<ProductItem> addProductItemsToShoppingCart(Long groupId, String shoppingCartName, List<ProductItemRequest> productItemList) {
        Optional<ShoppingCart> shoppingCart = shoppingCartRepository.findShoppingCartByName(shoppingCartName);
        if (shoppingCart.isEmpty()) {
            shoppingCart = Optional.ofNullable(shoppingCartService.addNewShoppingCart(shoppingCartName, groupId));
        }
        List<ProductItem> productItems = mapToListOfProductItem(shoppingCartName, productItemList, shoppingCart);
        log.info("Added new products to shopping Cart with name {} successfully", shoppingCartName);
        return productItemRepository.saveAll(productItems);
    }


    public ProductItem updateProductItemInShoppingCart(Long shoppingCartId, Long productId, ProductItemRequest updatedProductItem) {
        ProductItem productItem = getProductItemByIdFromShoppingCart(shoppingCartId, productId);
        productItem.setProductName(updatedProductItem.getProductName());
        productItem.setAmount(updatedProductItem.getAmount());
        productItem.setProductType(updatedProductItem.getProductType());
        return productItemRepository.save(productItem);
    }

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

    private List<ProductItem> mapToListOfProductItem(String shoppingCartName, List<ProductItemRequest> productItemList, Optional<ShoppingCart> shoppingCart) {
        List<ProductItem> productItems = new ArrayList<>();
        for (ProductItemRequest item : productItemList) {
            ProductItem productItem = ProductItem.builder()
                    .productName(shoppingCartName)
                    .productType(item.getProductType())
                    .amount(item.getAmount())
                    .shoppingCart(shoppingCart.orElse(null))
                    .build();
            productItems.add(productItem);
        }
        return productItems;
    }
}
