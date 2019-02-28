package com.videogamestore.service;

import java.util.List;

import com.videogamestore.domain.CartItem;
import com.videogamestore.domain.Game;
import com.videogamestore.domain.Order;
import com.videogamestore.domain.ShoppingCart;
import com.videogamestore.domain.User;

public interface CartItemService {
	List<CartItem> findByShoppingCart(ShoppingCart shoppingCart);
	CartItem updateCartItem(CartItem cartItem);
	
	CartItem addGameToCartItem(Game game, User user, int qty, boolean rewardDiscount);
	CartItem findById(Long cartItemId);
	void removeCartItem(CartItem cartItem);
	CartItem save(CartItem cartItem);
	List<CartItem> findByOrder(Order order);
	

}
