package com.videogamestore.service;

import com.videogamestore.domain.ShoppingCart;

public interface ShoppingCartService {

	ShoppingCart updateShoppingCart(ShoppingCart shoppingCart);
	void clearShoppingCart(ShoppingCart shoppingCart);
}
