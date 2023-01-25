package com.shop.list.shopappka.services;


import com.shop.list.shopappka.exceptions.ShoppingCartExistsException;
import com.shop.list.shopappka.exceptions.ShoppingCartNotFoundException;
import com.shop.list.shopappka.models.domain.Group;
import com.shop.list.shopappka.models.domain.ShoppingCart;
import com.shop.list.shopappka.repositories.ShoppingCartRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;

    private final GroupService groupService;

    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository, GroupService groupService) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.groupService = groupService;
    }

    public ShoppingCart addNewShoppingCart(String shoppingCartName, Long groupId) {
        if (shoppingCartRepository.findShoppingCartByName(shoppingCartName).isEmpty()) {
            Group group = groupService.getGroupById(groupId);
            ShoppingCart shoppingCart = ShoppingCart.builder()
                    .shoppingCartName(shoppingCartName)
                    .group(group).build();
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

    public List<ShoppingCart> getAllShoppingCartsByGroupId(Long groupId) {
        return shoppingCartRepository.findAllShoppingCartsByGroupId(groupId);
    }

    private ShoppingCart getShoppingCartById(Long id) {
        return shoppingCartRepository.findShoppingCartByShoppingCartId(id).orElseThrow(() -> {
            throw new ShoppingCartNotFoundException("Shopping Cart with id: " + id + " not found.");
        });
    }
}
