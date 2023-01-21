package com.shop.list.shopappka.services;


import com.shop.list.shopappka.exceptions.ShoppingCartExistsException;
import com.shop.list.shopappka.exceptions.ShoppingCartNotFoundException;
import com.shop.list.shopappka.models.domain.ShoppingCart;
import com.shop.list.shopappka.repositories.ShoppingCartRepository;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;

    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository) {
        this.shoppingCartRepository = shoppingCartRepository;
    }

    public ShoppingCart addNewShoppingCart(String shoppingCartName) {
        if (!shoppingCartRepository.existsByShoppingCartName(shoppingCartName)) {
            ShoppingCart shoppingCart = ShoppingCart.builder().shoppingCartName(shoppingCartName).build();
            return shoppingCartRepository.save(shoppingCart);
        } else {
            throw new ShoppingCartExistsException("Shopping Cart with name: " + shoppingCartName + " exists");
        }
    }

    public ShoppingCart updateNameOfShoppingCart(Long shoppingCartId, String updatedName) {
        ShoppingCart shoppingCart = getShoppingCartById(shoppingCartId);
        shoppingCart.setShoppingCartName(updatedName);
        return shoppingCartRepository.save(shoppingCart);
    }

    private ShoppingCart getShoppingCartById(Long id) {
        return shoppingCartRepository.findShoppingCartByShoppingCartId(id).orElseThrow(() -> {
            throw new ShoppingCartNotFoundException("Shopping Cart with id: " + id + " not found.");
        });
    }


}
